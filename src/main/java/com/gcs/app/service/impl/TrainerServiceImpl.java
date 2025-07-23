package com.gcs.app.service.impl;

import com.gcs.app.dao.TrainerDao;
import com.gcs.app.dao.transaction.TransactionalContext;
import com.gcs.app.exception.ServiceException;
import com.gcs.app.facade.dto.TrainerCreateRequestDto;
import com.gcs.app.facade.dto.TrainerTrainingSearchCriteriaDto;
import com.gcs.app.facade.dto.TrainerUpdateRequestDto;
import com.gcs.app.mapper.TrainerMapper;
import com.gcs.app.model.Trainee;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.Training;
import com.gcs.app.model.TrainingType;
import com.gcs.app.model.User;
import com.gcs.app.service.TrainerService;
import com.gcs.app.service.TrainingTypeService;
import com.gcs.app.service.UserService;
import com.gcs.app.service.common.CredentialsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class TrainerServiceImpl implements TrainerService {

    private final TrainerDao trainerDao;
    private final UserService userService;
    private final TrainingTypeService trainingTypeService;
    private final CredentialsService credentialsService;
    private final TrainerMapper trainerMapper;

    @TransactionalContext
    @Override
    public Trainer createTrainer(@Valid TrainerCreateRequestDto trainerCreateRequestDto) {
        String specializationName = trainerCreateRequestDto.getSpecialization().getName();
        TrainingType specialization = trainingTypeService.getByName(specializationName);

        Trainer trainer = trainerMapper.toEntity(trainerCreateRequestDto);
        User user = trainer.getUser();
        log.info("Creating trainer: {} {}", user.getFirstName(), user.getLastName());

        String username = credentialsService.generateUsername(user.getFirstName(), user.getLastName(), userService.getAllUsernames());
        String password = credentialsService.generateRandomPassword();
        String encryptedPassword = credentialsService.encodePassword(password);

        Trainer trainerWithCredentials = trainer.toBuilder()
                .user(userWithCredentials(user, username, encryptedPassword))
                .specialization(specialization)
                .build();

        Trainer createdTrainer = trainerDao.create(trainerWithCredentials);
        log.info("Trainer created: {}", createdTrainer);

        return createdTrainer.toBuilder()
                .user(userWithCredentials(user, username, password))
                .build();
    }

    @TransactionalContext
    @Override
    public Trainer updateTrainer(@Valid TrainerUpdateRequestDto dto) {
        String username = dto.getUsername();
        Trainer existing = trainerDao.findByUsername(username)
                .orElseThrow(() -> new ServiceException(String.format("Trainer with username %s not found", username)));

        Trainer updated = trainerMapper.update(existing, dto);

        return trainerDao.update(updated);
    }

    @TransactionalContext(readOnly = true)
    @Override
    public Trainer getByUsername(String username) {
        log.info("Getting trainer by username: {}", username);

        return trainerDao.findByUsername(username)
                .orElseThrow(() -> new ServiceException(String.format("Trainer not found with username: %s", username)));
    }

    @TransactionalContext(readOnly = true)
    @Override
    public List<Training> getTrainerTrainings(@Valid TrainerTrainingSearchCriteriaDto criteria) {
        log.info("Searching trainings with criteria: {}", criteria);

        return trainerDao.findByTrainerCriteria(criteria);
    }

    @TransactionalContext
    @Override
    public void setTrainerActivationStatus(String username, boolean isActive) {
        Trainer trainer = trainerDao.findByUsername(username)
                .orElseThrow(() -> new ServiceException(String.format("Trainer not found with username: %s", username)));

        User updatedUser = trainer.getUser().toBuilder()
                .isActive(isActive)
                .build();
        Trainer updatedTrainer = trainer.toBuilder()
                .user(updatedUser)
                .build();

        trainerDao.update(updatedTrainer);

        log.info("Trainer {} set to {}", username, isActive ? "active" : "inactive");
    }

    @TransactionalContext(readOnly = true)
    @Override
    public List<Trainer> getUnassignedForTrainee(Trainee trainee) {
        return trainerDao.findAllNotAssignedToTrainee(trainee);
    }

    private User userWithCredentials(User user, String username, String password) {
        return User.builder()
                .username(username)
                .password(password)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .isActive(true)
                .build();
    }
}