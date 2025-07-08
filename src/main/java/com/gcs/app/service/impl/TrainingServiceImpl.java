package com.gcs.app.service.impl;

import com.gcs.app.dao.TrainingDao;
import com.gcs.app.facade.dto.TrainingCreateRequestDto;
import com.gcs.app.exception.ServiceException;
import com.gcs.app.mapper.TrainingMapper;
import com.gcs.app.model.Training;
import com.gcs.app.service.TraineeService;
import com.gcs.app.service.TrainerService;
import com.gcs.app.service.TrainingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class TrainingServiceImpl implements TrainingService {

    private final TrainingDao trainingDao;
    private final TrainingMapper trainingMapper;
    private final TraineeService traineeService;
    private final TrainerService trainerService;

    @Override
    public Training createTraining(@Valid TrainingCreateRequestDto createRequestDto) {
        Training training = trainingMapper.toEntity(createRequestDto);
        log.info("Creating training: {}", training.getName());

        validateTraineeExists(training.getTrainee().getId());
        validateTrainerExists(training.getTrainer().getId());

        Training createdTraining = trainingDao.create(training);
        log.debug("Training created: {}", createdTraining);

        return createdTraining;
    }

    @Override
    public Training getTraining(Long id) {
        log.info("Retrieving training with id: {}", id);

        Training training = validateTrainingExists(id).orElseThrow(() -> new ServiceException(String.format("Training with id {} not found", id)));
        log.debug("Training retrieved: {}", training);

        return training;
    }

    private Optional<Training> validateTrainingExists(Long id) {
        Optional<Training> training = trainingDao.get(id);

        if (training.isEmpty()) {
            throw new ServiceException(String.format("Training with id %d not found", id));
        }

        return training;
    }

    private void validateTraineeExists(Long traineeId) {

        if (traineeService.getTrainee(traineeId) == null) {
            throw new ServiceException(String.format("Trainee with id %d not found", traineeId));
        }
    }

    private void validateTrainerExists(Long trainerId) {

        if (trainerService.getTrainer(trainerId) == null) {
            throw new ServiceException(String.format("Trainer with id %d not found", trainerId));
        }
    }
}