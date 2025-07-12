package com.gcs.app.service.impl;

import com.gcs.app.dao.TraineeDao;
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
import com.gcs.app.util.EntityAssociationHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Override
    public Trainee createTrainee(@Valid TraineeCreateRequestDto requestDto) {
        Trainee trainee = traineeMapper.toEntity(requestDto);
        log.info("Creating trainee: {} {}", trainee.getUser().getFirstName(), trainee.getUser().getLastName());

        Trainee traineeWithCredentials = trainee.toBuilder()
                .user(userWithCredentials(trainee.getUser()))
                .build();

        Trainee createdTrainee = traineeDao.create(traineeWithCredentials);
        log.debug("Trainee created: {}", createdTrainee);

        return createdTrainee;
    }

    @Override
    public Trainee updateTrainee(@Valid TraineeUpdateRequestDto dto) {
        String username = dto.getUsername();
        Trainee existing = traineeDao.findByUsername(username)
                .orElseThrow(() -> new ServiceException(String.format("Trainee with username %s not found", username)));

        Trainee updated = traineeMapper.update(existing, dto);

        return traineeDao.update(updated);
    }

    @Override
    public void deleteTraineeByUsername(String username) {
        log.info("Deleting trainee with username: {}", username);

        traineeDao.findByUsername(username)
                .orElseThrow(() -> new ServiceException(String.format("Trainee with username %s not found", username)));

        traineeDao.deleteByUsername(username);
        log.debug("Trainee with username {} deleted", username);
    }

    @Override
    public Trainee getByUsername(String username) {
        log.info("Getting trainee by username: {}", username);

        return traineeDao.findByUsername(username)
                .orElseThrow(() -> new ServiceException(String.format("Trainee not found with username: %s", username)));
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

    @Override
    public List<Training> getTraineeTrainings(@Valid TraineeTrainingSearchCriteriaDto criteria) {
        log.info("Searching trainings with criteria: {}", criteria);

        return traineeDao.findByTraineeCriteria(criteria);
    }

    @Override
    public Trainee setTraineeActive(String username, boolean isActive) {
        Trainee trainee = traineeDao.findByUsername(username)
                .orElseThrow(() -> new ServiceException(String.format("Trainee not found with username: %s", username)));

        User updatedUser = trainee.getUser().toBuilder()
                .isActive(isActive)
                .build();

        Trainee updatedTrainee = trainee.toBuilder()
                .user(updatedUser)
                .build();

        Trainee result = traineeDao.update(updatedTrainee);
        log.info("Trainee {} set to {}", username, isActive ? "active" : "inactive");

        return result;
    }

    @Override
    public List<Trainer> getUnassignedTrainers(String traineeUsername) {
        Trainee trainee = traineeDao.findByUsername(traineeUsername)
                .orElseThrow(() -> new ServiceException(String.format("Trainee not found with username: %s", traineeUsername)));

        return trainerService.getUnassignedForTrainee(trainee);
    }

    @Override
    public Trainee updateTraineeTrainers(String traineeUsername, List<String> trainerUsernames) {
        Trainee trainee = traineeDao.findByUsername(traineeUsername)
                .orElseThrow(() -> new ServiceException(String.format("Trainee not found with username: %s", traineeUsername)));

        Set<Trainer> newTrainers = trainerUsernames.stream()
                .map(trainerService::getByUsername)
                .collect(Collectors.toSet());

        EntityAssociationHelper.setTraineeTrainers(trainee, newTrainers);

        return traineeDao.update(trainee);
    }

}