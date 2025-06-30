package com.gcs.app.facade;

import com.gcs.app.facade.dto.TraineeCreateRequestDto;
import com.gcs.app.facade.dto.TraineeResponseDto;
import com.gcs.app.facade.dto.TraineeUpdateRequestDto;
import com.gcs.app.facade.dto.TrainerCreateRequestDto;
import com.gcs.app.facade.dto.TrainerResponseDto;
import com.gcs.app.facade.dto.TrainerUpdateRequestDto;
import com.gcs.app.facade.dto.TrainingCreateRequestDto;
import com.gcs.app.facade.dto.TrainingResponseDto;
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

    public TraineeResponseDto createTrainee(TraineeCreateRequestDto traineeCreateRequestDto) {
        log.info("Creating trainee: {} {}", traineeCreateRequestDto.getFirstName(), traineeCreateRequestDto.getLastName());

        Trainee saved = traineeService.createTrainee(traineeCreateRequestDto);

        return traineeMapper.toDto(saved);
    }

    public TraineeResponseDto updateTrainee(TraineeUpdateRequestDto traineeUpdateRequestDto) {
        log.info("Updating trainee with userId: {}", traineeUpdateRequestDto.getUserId());

        Trainee updated = traineeService.updateTrainee(traineeUpdateRequestDto);

        return traineeMapper.toDto(updated);
    }

    public void deleteTrainee(Long userId) {
        log.info("Deleting trainee with userId: {}", userId);

        traineeService.deleteTrainee(userId);
    }

    public TraineeResponseDto getTrainee(Long userId) {
        log.info("Retrieving trainee with userId: {}", userId);

        return traineeMapper.toDto(traineeService.getTrainee(userId));
    }

    public TrainerResponseDto createTrainer(TrainerCreateRequestDto trainerCreateRequestDto) {
        log.info("Creating trainer: {} {}", trainerCreateRequestDto.getFirstName(), trainerCreateRequestDto.getLastName());

        Trainer saved = trainerService.createTrainer(trainerCreateRequestDto);

        return trainerMapper.toDto(saved);
    }

    public TrainerResponseDto updateTrainer(TrainerUpdateRequestDto trainerUpdateRequestDto) {
        log.info("Updating trainer with userId: {}", trainerUpdateRequestDto.getUserId());

        Trainer updated = trainerService.updateTrainer(trainerUpdateRequestDto);

        return trainerMapper.toDto(updated);
    }

    public TrainerResponseDto getTrainer(Long userId) {
        log.info("Retrieving trainer with userId: {}", userId);

        return trainerMapper.toDto(trainerService.getTrainer(userId));
    }

    public TrainingResponseDto createTraining(TrainingCreateRequestDto trainingCreateRequestDto) {
        log.info("Creating training: {}", trainingCreateRequestDto.getName());

        Training saved = trainingService.createTraining(trainingCreateRequestDto);

        return trainingMapper.toDto(saved);
    }

    public TrainingResponseDto getTraining(Long id) {
        log.info("Retrieving training with id: {}", id);

        return trainingMapper.toDto(trainingService.getTraining(id));
    }
}
