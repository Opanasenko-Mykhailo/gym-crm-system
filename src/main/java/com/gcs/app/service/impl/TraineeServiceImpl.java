package com.gcs.app.service.impl;

import com.gcs.app.dao.TraineeDao;
import com.gcs.app.dao.transaction.TransactionalContext;
import com.gcs.app.exception.ServiceException;
import com.gcs.app.facade.dto.TraineeCreateRequestDto;
import com.gcs.app.facade.dto.TraineeTrainingSearchCriteriaDto;
import com.gcs.app.facade.dto.TraineeUpdateRequestDto;
import com.gcs.app.mapper.TraineeMapper;
import com.gcs.app.model.Trainee;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.Training;
import com.gcs.app.model.User;
import com.gcs.app.service.TraineeService;
import com.gcs.app.service.TrainerService;
import com.gcs.app.service.UserService;
import com.gcs.app.service.common.CredentialsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class TraineeServiceImpl implements TraineeService {

    private final TraineeDao traineeDao;
    private final UserService userService;
    private final TrainerService trainerService;
    private final CredentialsService credentialsService;
    private final TraineeMapper traineeMapper;

    @TransactionalContext
    @Override
    public Trainee createTrainee(@Valid TraineeCreateRequestDto requestDto) {
        Trainee trainee = traineeMapper.toEntity(requestDto);
        User user = trainee.getUser();
        log.info("Creating trainee: {} {}", user.getFirstName(), user.getLastName());

        String username = credentialsService.generateUsername(user.getFirstName(), user.getLastName(), userService.getAllUsernames());
        String password = credentialsService.generateRandomPassword();
        String encryptedPassword = credentialsService.encodePassword(password);


        Trainee traineeWithCredentials = trainee.toBuilder()
                .user(userWithCredentials(user, username, encryptedPassword))
                .build();

        Trainee createdTrainee = traineeDao.create(traineeWithCredentials);
        log.info("Trainee created with username: {}", username);

        return createdTrainee.toBuilder()
                .user(userWithCredentials(user, username, password))
                .build();
    }

    @TransactionalContext
    @Override
    public Trainee updateTrainee(@Valid TraineeUpdateRequestDto dto) {
        String username = dto.getUsername();
        Trainee existing = traineeDao.findByUsername(username)
                .orElseThrow(() -> new ServiceException(String.format("Trainee with username %s not found", username)));

        Trainee updated = buildUpdatedTrainee(existing, dto);

        return traineeDao.update(updated);
    }

    @TransactionalContext
    @Override
    public void deleteTraineeByUsername(String username) {
        log.info("Deleting trainee with username: {}", username);

        traineeDao.findByUsername(username)
                .orElseThrow(() -> new ServiceException(String.format("Trainee with username %s not found", username)));

        traineeDao.deleteByUsername(username);
        log.debug("Trainee with username {} deleted", username);
    }

    @TransactionalContext(readOnly = true)
    @Override
    public Trainee getByUsername(String username) {
        log.info("Getting trainee by username: {}", username);

        return traineeDao.findByUsername(username)
                .orElseThrow(() -> new ServiceException(String.format("Trainee not found with username: %s", username)));
    }

    @TransactionalContext(readOnly = true)
    @Override
    public List<Training> getTraineeTrainings(@Valid TraineeTrainingSearchCriteriaDto criteria) {
        log.info("Searching trainings with criteria: {}", criteria);

        return traineeDao.findByTraineeCriteria(criteria);
    }

    @TransactionalContext(readOnly = true)
    @Override
    public void setTraineeActivationStatus(String username, boolean isActive) {
        Trainee trainee = traineeDao.findByUsername(username)
                .orElseThrow(() -> new ServiceException(String.format("Trainee not found with username: %s", username)));

        User updatedUser = trainee.getUser().toBuilder()
                .isActive(isActive)
                .build();
        Trainee updatedTrainee = trainee.toBuilder()
                .user(updatedUser)
                .build();

        traineeDao.update(updatedTrainee);
        log.info("Trainee {} set to {}", username, isActive ? "active" : "inactive");
    }

    @TransactionalContext(readOnly = true)
    @Override
    public List<Trainer> getUnassignedTrainers(String traineeUsername) {
        Trainee trainee = traineeDao.findByUsername(traineeUsername)
                .orElseThrow(() -> new ServiceException(String.format("Trainee not found with username: %s", traineeUsername)));

        return trainerService.getUnassignedForTrainee(trainee);
    }

    @TransactionalContext
    @Override
    public Trainee updateTraineeTrainers(String traineeUsername, List<String> trainerUsernames) {
        Trainee trainee = traineeDao.findByUsername(traineeUsername)
                .orElseThrow(() -> new ServiceException(String.format("Trainee not found with username: %s", traineeUsername)));

        Set<Trainer> newTrainers = trainerUsernames.stream()
                .map(trainerService::getByUsername)
                .collect(Collectors.toSet());

        setTraineeTrainers(trainee, newTrainers);

        return traineeDao.update(trainee);
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

    private Trainee buildUpdatedTrainee(Trainee trainee, TraineeUpdateRequestDto dto) {
        User.UserBuilder userBuilder = trainee.getUser().toBuilder();
        ofNullable(dto.getFirstName())
                .ifPresent(userBuilder::firstName);
        ofNullable(dto.getLastName())
                .ifPresent(userBuilder::lastName);
        ofNullable(dto.getUsername())
                .ifPresent(userBuilder::username);
        ofNullable(dto.getIsActive())
                .ifPresent(userBuilder::isActive);

        Trainee.TraineeBuilder traineeBuilder = trainee.toBuilder()
                .user(userBuilder.build())
                .dateOfBirth(dto.getDateOfBirth())
                .address(dto.getAddress());

        return traineeBuilder.build();
    }

    private void addTrainerToTrainee(Trainee trainee, Trainer trainer) {
        if (!trainee.getTrainers().contains(trainer)) {
            trainee.getTrainers().add(trainer);
        }

        if (!trainer.getTrainees().contains(trainee)) {
            trainer.getTrainees().add(trainee);
        }
    }

    private void removeTrainerFromTrainee(Trainee trainee, Trainer trainer) {
        trainee.getTrainers().remove(trainer);
        trainer.getTrainees().remove(trainee);
    }

    private void setTraineeTrainers(Trainee trainee, Set<Trainer> newTrainers) {
        new HashSet<>(trainee.getTrainers())
                .forEach(oldTrainer -> removeTrainerFromTrainee(trainee, oldTrainer));

        newTrainers.forEach(trainer -> addTrainerToTrainee(trainee, trainer));
    }
}