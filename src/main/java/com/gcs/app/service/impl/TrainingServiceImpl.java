package com.gcs.app.service.impl;

import com.gcs.app.dao.TrainingDao;
import com.gcs.app.exception.ServiceException;
import com.gcs.app.model.Training;
import com.gcs.app.service.TrainingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingServiceImpl implements TrainingService {

    private final TrainingDao trainingDao;

    @Override
    public Training createTraining(Training training) {
        log.info("Creating training: {}", training.getName());

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
            throw new ServiceException(String.format("Training with id {} not found", id));
        }

        return training;
    }
}