package com.gcs.app.service.impl;

import com.gcs.app.dao.TraineeDao;
import com.gcs.app.dao.UserDao;
import com.gcs.app.exception.ServiceException;
import com.gcs.app.facade.dto.TraineeCreateRequestDto;
import com.gcs.app.facade.dto.TraineeUpdateRequestDto;
import com.gcs.app.mapper.TraineeMapper;
import com.gcs.app.model.Trainee;
import com.gcs.app.model.User;
import com.gcs.app.service.TraineeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

import static com.gcs.app.util.UserUtils.generateRandomPassword;
import static com.gcs.app.util.UserUtils.generateUsername;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class TraineeServiceImpl implements TraineeService {

    private final TraineeDao traineeDao;
    private final UserDao userDao;
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
    public Trainee updateTrainee(@Valid TraineeUpdateRequestDto traineeUpdateRequestDto) {
        Trainee updatedTrainee = traineeMapper.toUpdateEntity(traineeUpdateRequestDto);

        Long userId = updatedTrainee.getId();
        log.info("Updating trainee with userId: {}", userId);

        validateTraineeExists(userId);

        Trainee traineeWithId = updatedTrainee.toBuilder().id(userId).build();
        Trainee savedTrainee = traineeDao.update(traineeWithId);
        log.debug("Trainee updated: {}", savedTrainee);

        return savedTrainee;
    }

    @Override
    public void deleteTrainee(Long userId) {
        log.info("Deleting trainee with userId: {}", userId);

        validateTraineeExists(userId);

        traineeDao.delete(userId);
        log.debug("Trainee with userId {} deleted", userId);
    }

    @Override
    public void deleteTraineeByUsername(String username) {
        log.info("Deleting trainee with username: {}", username);

        Trainee trainee = traineeDao.findByUsername(username)
                .orElseThrow(() -> new ServiceException(String.format("Trainee with username %s not found", username)));

        traineeDao.delete(trainee.getId());

        log.debug("Trainee with username {} deleted", username);
    }

    @Override
    public Trainee getTrainee(Long userId) {
        log.info("Retrieving trainee with userId: {}", userId);

        Trainee trainee = validateTraineeExists(userId).orElseThrow(() -> new ServiceException(String.format("Trainee with userId {} not found", userId)));
        log.debug("Trainee retrieved: {}", trainee);

        return trainee;
    }

    @Override
    public Trainee getByUsername(String username) {
        log.info("Getting trainee by username: {}", username);

        return traineeDao.findByUsername(username)
                .orElseThrow(() -> new ServiceException(String.format("Trainee not found with username: %s", username)));
    }

    @Override
    public boolean authenticateTrainee(String username, String password) {
        log.info("Authenticating trainee with username: {}", username);

        return traineeDao.findByUsername(username)
                .map(t -> {
                    boolean matches = t.getUser().getPassword().equals(password);
                    log.info("Trainee {} authentication {}", username, matches ? "successful" : "failed: wrong password");

                    return matches;
                })
                .orElseGet(() -> {
                    log.warn("Trainee not found for username: {}", username);

                    return false;
                });
    }

    private Optional<Trainee> validateTraineeExists(Long userId) {
        Optional<Trainee> trainee = traineeDao.get(userId);

        if (trainee.isEmpty()) {
            throw new ServiceException(String.format("Trainee with userId %d not found", userId));
        }

        return trainee;
    }

    private User userWithCredentials(User user) {
        String username = generateUsername(user.getFirstName(), user.getLastName(), userDao.findAllUsernames());
        String password = generateRandomPassword();

        return user.builder()
                .username(username)
                .password(password)
                .isActive(true)
                .build();
    }
}