package com.gcs.app.facade;

import com.gcs.app.facade.dto.PasswordChangeRequestDto;
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
        log.info("Updating trainee with username: {}", traineeUpdateRequestDto.getUsername());
        Trainee updated = traineeService.updateTrainee(traineeUpdateRequestDto);
        return traineeMapper.toDto(updated);
    }

    public void deleteTraineeByUsername(String username) {
        log.info("Deleting trainee with username: {}", username);
        traineeService.deleteTraineeByUsername(username);
    }

    public TraineeResponseDto getTraineeByUsername(String username) {
        log.info("Retrieving trainee by username: {}", username);
        Trainee trainee = traineeService.getByUsername(username);
        return traineeMapper.toDto(trainee);
    }

    public void changeTraineePassword(PasswordChangeRequestDto dto) {
        log.info("Changing password for trainee with username: {}", dto.getUsername());
        traineeService.changePassword(dto);
    }

    public TrainerResponseDto createTrainer(TrainerCreateRequestDto trainerCreateRequestDto) {
        log.info("Creating trainer: {} {}", trainerCreateRequestDto.getFirstName(), trainerCreateRequestDto.getLastName());
        Trainer saved = trainerService.createTrainer(trainerCreateRequestDto);
        return trainerMapper.toDto(saved);
    }

    public TrainerResponseDto updateTrainer(TrainerUpdateRequestDto trainerUpdateRequestDto) {
        log.info("Updating trainer with username: {}", trainerUpdateRequestDto.getUsername());
        Trainer updated = trainerService.updateTrainer(trainerUpdateRequestDto);
        return trainerMapper.toDto(updated);
    }

    public TrainerResponseDto getTrainerByUsername(String username) {
        log.info("Retrieving trainer by username: {}", username);
        Trainer trainer = trainerService.getByUsername(username);
        return trainerMapper.toDto(trainer);
    }

    public void changeTrainerPassword(PasswordChangeRequestDto dto) {
        log.info("Changing password for trainer with username: {}", dto.getUsername());
        trainerService.changePassword(dto);
    }

    public TrainingResponseDto createTraining(TrainingCreateRequestDto trainingCreateRequestDto) {
        log.info("Creating training: {}", trainingCreateRequestDto.getName());
        Training saved = trainingService.createTraining(trainingCreateRequestDto);
        return trainingMapper.toDto(saved);
    }

    public TrainingResponseDto getTraining(Long id) {
        log.info("Retrieving training with id: {}", id);
        Training training = trainingService.getTraining(id);
        return trainingMapper.toDto(training);
    }
}
