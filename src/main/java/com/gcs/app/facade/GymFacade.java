package com.gcs.app.facade;


import com.gcs.app.dto.TraineeDto;
import com.gcs.app.dto.TrainerDto;
import com.gcs.app.dto.TrainingDto;
import com.gcs.app.mapper.TraineeMapper;
import com.gcs.app.mapper.TrainerMapper;
import com.gcs.app.mapper.TrainingMapper;
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
    private final TraineeMapper traineeMapper;
    private final TrainerMapper trainerMapper;
    private final TrainingMapper trainingMapper;

    public TraineeDto createTrainee(TraineeDto traineeDto) {
        log.info("Creating trainee: {} {}", traineeDto.getFirstName(), traineeDto.getLastName());

        Trainee trainee = traineeMapper.toEntity(traineeDto);
        Trainee saved = traineeService.createTrainee(trainee);

        return traineeMapper.toDto(saved);
    }

    public TraineeDto updateTrainee(TraineeDto updatedDto) {
        Long userId = updatedDto.getUserId();
        log.info("Updating trainee with userId: {}", userId);

        Trainee updatedEntity = traineeMapper.toEntity(updatedDto);
        Trainee updated = traineeService.updateTrainee(updatedEntity);

        return traineeMapper.toDto(updated);
    }

    public void deleteTrainee(Long userId) {
        log.info("Deleting trainee with userId: {}", userId);

        traineeService.deleteTrainee(userId);
    }

    public TraineeDto getTrainee(Long userId) {
        log.info("Retrieving trainee with userId: {}", userId);

        return traineeMapper.toDto(traineeService.getTrainee(userId));
    }

    public TrainerDto createTrainer(TrainerDto trainerDto) {
        log.info("Creating trainer: {} {}", trainerDto.getFirstName(), trainerDto.getLastName());

        Trainer trainer = trainerMapper.toEntity(trainerDto);
        Trainer saved = trainerService.createTrainer(trainer);

        return trainerMapper.toDto(saved);
    }

    public TrainerDto updateTrainer(TrainerDto updatedDto) {
        Long userId =updatedDto.getUserId();
        log.info("Updating trainer with userId: {}", userId);

        Trainer updatedEntity = trainerMapper.toEntity(updatedDto);
        Trainer updated = trainerService.updateTrainer(updatedEntity);

        return trainerMapper.toDto(updated);
    }

    public TrainerDto getTrainer(Long userId) {
        log.info("Retrieving trainer with userId: {}", userId);

        return trainerMapper.toDto(trainerService.getTrainer(userId));
    }

    public TrainingDto createTraining(TrainingDto trainingDto) {
        log.info("Creating training: {}", trainingDto.getName());

        Training training = trainingMapper.toEntity(trainingDto);
        Training saved = trainingService.createTraining(training);

        return trainingMapper.toDto(saved);
    }

    public TrainingDto getTraining(Long id) {
        log.info("Retrieving training with id: {}", id);

        return trainingMapper.toDto(trainingService.getTraining(id));
    }
}
