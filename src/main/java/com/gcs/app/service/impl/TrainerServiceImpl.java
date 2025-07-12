package com.gcs.app.service.impl;

import com.gcs.app.dao.TrainerDao;
import com.gcs.app.exception.ServiceException;
import com.gcs.app.facade.dto.TrainerCreateRequestDto;
import com.gcs.app.facade.dto.TrainerTrainingSearchCriteriaDto;
import com.gcs.app.facade.dto.TrainerUpdateRequestDto;
import com.gcs.app.mapper.TrainerMapper;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.Training;
import com.gcs.app.model.User;
import com.gcs.app.service.CredentialsService;
import com.gcs.app.service.TrainerService;
import com.gcs.app.service.UserService;
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
    private final CredentialsService credentialsService;
    private final TrainerMapper trainerMapper;

    @Override
    public Trainer createTrainer(@Valid TrainerCreateRequestDto trainerCreateRequestDto) {
        Trainer trainer = trainerMapper.toEntity(trainerCreateRequestDto);
        log.info("Creating trainer: {} {}", trainer.getUser().getFirstName(), trainer.getUser().getLastName());

        Trainer trainerWithCredentials = trainer.toBuilder()
                .user(userWithCredentials(trainer.getUser()))
                .build();

        Trainer createdTrainer = trainerDao.create(trainerWithCredentials);
        log.debug("Trainer created: {}", createdTrainer);

        return createdTrainer;
    }

    @Override
    public Trainer updateTrainer(@Valid TrainerUpdateRequestDto dto) {
        String username = dto.getUsername();
        Trainer existing = trainerDao.findByUsername(username)
                .orElseThrow(() -> new ServiceException(String.format("Trainer with username %s not found", username)));

        Trainer updated = trainerMapper.update(existing, dto);

        return trainerDao.update(updated);
    }

    @Override
    public Trainer getByUsername(String username) {
        log.info("Getting trainer by username: {}", username);

        return trainerDao.findByUsername(username)
                .orElseThrow(() -> new ServiceException(String.format("Trainer not found with username: %s", username)));
    }

    @Override
    public List<Training> getTrainerTrainings(@Valid TrainerTrainingSearchCriteriaDto criteria) {
        log.info("Searching trainings with criteria: {}", criteria);

        return trainerDao.findByTrainerCriteria(criteria);
    }

    private User userWithCredentials(User user) {
        String username = credentialsService.generateUsername(user.getFirstName(), user.getLastName(), userService.getAllUsernames());
        String password = credentialsService.generateRandomPassword();

        return User.builder()
                .username(username)
                .password(password)
                .isActive(true)
                .build();
    }
}