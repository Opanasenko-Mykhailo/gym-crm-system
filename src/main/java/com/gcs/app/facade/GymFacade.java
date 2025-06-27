package com.gcs.app.facade;

import com.gcs.app.model.Trainee;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.Training;
import com.gcs.app.service.TraineeService;
import com.gcs.app.service.TrainerService;
import com.gcs.app.service.TrainingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public Trainee createTrainee(Trainee trainee) {
        log.info("Creating trainee: {} {}", trainee.getFirstName(), trainee.getLastName());

        return traineeService.createTrainee(trainee);
    }

    public Trainee updateTrainee(Long userId, Trainee updatedTrainee) {
        log.info("Updating trainee with userId: {}", userId);

        return traineeService.updateTrainee(userId, updatedTrainee);
    }

    public void deleteTrainee(Long userId) {
        log.info("Deleting trainee with userId: {}", userId);

        traineeService.deleteTrainee(userId);
    }

    public Trainee getTrainee(Long userId) {
        log.info("Retrieving trainee with userId: {}", userId);

        return traineeService.getTrainee(userId);
    }

    public Trainer createTrainer(Trainer trainer) {
        log.info("Creating trainer: {} {}", trainer.getFirstName(), trainer.getLastName());

        return trainerService.createTrainer(trainer);
    }

    public Trainer updateTrainer(Long userId, Trainer updatedTrainer) {
        log.info("Updating trainer with userId: {}", userId);

        return trainerService.updateTrainer(userId, updatedTrainer);
    }

    public Trainer getTrainer(Long userId) {
        log.info("Retrieving trainer with userId: {}", userId);

        return trainerService.getTrainer(userId);
    }

    public Training createTraining(Training training) {
        log.info("Creating training: {}", training.getName());

        return trainingService.createTraining(training);
    }

    public Training getTraining(Long id) {
        log.info("Retrieving training with id: {}", id);

        return trainingService.getTraining(id);
    }
}