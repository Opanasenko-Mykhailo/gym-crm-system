package com.gcs.app.service.impl;

import com.gcs.app.dao.TraineeDao;
import com.gcs.app.facade.dto.TraineeCreateRequestDto;
import com.gcs.app.facade.dto.TraineeUpdateRequestDto;
import com.gcs.app.exception.ServiceException;
import com.gcs.app.mapper.TraineeMapper;
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
    private final TraineeMapper traineeMapper;

    @Override
    public Trainee createTrainee(TraineeCreateRequestDto requestDto) {
        Trainee trainee = traineeMapper.toEntity(requestDto);

        log.info("Creating trainee: {} {}", trainee.getFirstName(), trainee.getLastName());

        String username = generateUsername(trainee.getFirstName(), trainee.getLastName(), traineeDao.getAllUsernames());
        String password = generateRandomPassword();

        trainee.setUsername(username);
        trainee.setPassword(password);
        trainee.setIsActive(true);

        Trainee createdTrainee = traineeDao.create(trainee);
        log.debug("Trainee created: {}", createdTrainee);

        return createdTrainee;
    }


    @Override
    public Trainee updateTrainee(TraineeUpdateRequestDto traineeUpdateRequestDto) {
        Trainee updatedTrainee = traineeMapper.toUpdateEntity(traineeUpdateRequestDto);

        Long userId = updatedTrainee.getUserId();
        log.info("Updating trainee with userId: {}", userId);

        validateTraineeExists(userId);

        updatedTrainee.setUserId(userId);

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