package com.gcs.app.service.impl;

import com.gcs.app.dao.TraineeDao;
import com.gcs.app.exception.ServiceException;
import com.gcs.app.model.Trainee;
import com.gcs.app.service.TraineeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.gcs.app.util.UserUtils.generateRandomPassword;
import static com.gcs.app.util.UserUtils.generateUsername;

@Service
@RequiredArgsConstructor
@Slf4j
public class TraineeServiceImpl implements TraineeService {

    private final TraineeDao traineeDao;

    @Override
    public Trainee createTrainee(Trainee trainee) {
        log.info("Creating trainee: {} {}", trainee.getFirstName(), trainee.getLastName());

        trainee.setUsername(generateUsername(trainee.getFirstName(), trainee.getLastName(), traineeDao.getAllUsernames()));
        trainee.setPassword(generateRandomPassword());
        trainee.setIsActive(true);

        Trainee createdTrainee = traineeDao.create(trainee);
        log.debug("Trainee created: {}", createdTrainee);

        return createdTrainee;
    }

    @Override
    public Trainee updateTrainee(Long userId, Trainee updatedTrainee) {
        log.info("Updating trainee with userId: {}", userId);

        validateTraineeExists(userId);

        updatedTrainee.setUserId(userId);
        updatedTrainee.setUsername(generateUsername(updatedTrainee.getFirstName(), updatedTrainee.getLastName(), traineeDao.getAllUsernames()));

        Trainee savedTrainee = traineeDao.update(updatedTrainee);
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
    public Trainee getTrainee(Long userId) {
        log.info("Retrieving trainee with userId: {}", userId);

        Trainee trainee = validateTraineeExists(userId).orElseThrow(() -> new ServiceException(String.format("Trainee with userId {} not found", userId)));
        log.debug("Trainee retrieved: {}", trainee);

        return trainee;
    }

    private Optional<Trainee> validateTraineeExists(Long userId) {
        Optional<Trainee> trainee = traineeDao.get(userId);

        if (trainee.isEmpty()) {
            throw new ServiceException(String.format("Trainee with userId {} not found", userId));
        }

        return trainee;
    }
}