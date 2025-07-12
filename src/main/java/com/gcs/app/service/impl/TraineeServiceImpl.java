package com.gcs.app.service.impl;

import com.gcs.app.dao.TraineeDao;
import com.gcs.app.exception.ServiceException;
import com.gcs.app.facade.dto.TraineeCreateRequestDto;
import com.gcs.app.facade.dto.TraineeTrainingSearchCriteriaDto;
import com.gcs.app.facade.dto.TraineeUpdateRequestDto;
import com.gcs.app.mapper.TraineeMapper;
import com.gcs.app.model.Trainee;
import com.gcs.app.model.Training;
import com.gcs.app.model.User;
import com.gcs.app.service.CredentialsService;
import com.gcs.app.service.TraineeService;
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
public class TraineeServiceImpl implements TraineeService {

    private final TraineeDao traineeDao;
    private final UserService userService;
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
}