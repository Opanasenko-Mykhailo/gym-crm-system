package com.gcs.app.facade;

import com.gcs.app.facade.dto.AuthRequestDto;
import com.gcs.app.facade.dto.AuthResponseDto;
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
import com.gcs.app.security.Authenticated;
import com.gcs.app.security.CheckOwnProfile;
import com.gcs.app.service.AuthService;
import com.gcs.app.service.TraineeService;
import com.gcs.app.service.TrainerService;
import com.gcs.app.service.TrainingService;
import com.gcs.app.service.UserService;
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
    private final UserService userService;
    private final AuthService authService;
    private final TraineeMapper traineeMapper;
    private final TrainerMapper trainerMapper;
    private final TrainingMapper trainingMapper;

    public TraineeResponseDto createTrainee(TraineeCreateRequestDto traineeCreateRequestDto) {
        log.info("Creating trainee: {} {}", traineeCreateRequestDto.getFirstName(), traineeCreateRequestDto.getLastName());
        Trainee saved = traineeService.createTrainee(traineeCreateRequestDto);

        return traineeMapper.toDto(saved);
    }

    @Authenticated
    @CheckOwnProfile(usernameParam = "traineeUpdateRequestDto")
    public TraineeResponseDto updateTrainee(TraineeUpdateRequestDto traineeUpdateRequestDto) {
        log.info("Updating trainee with username: {}", traineeUpdateRequestDto.getUsername());
        Trainee updated = traineeService.updateTrainee(traineeUpdateRequestDto);

        return traineeMapper.toDto(updated);
    }

    @Authenticated
    public void deleteTraineeByUsername(String username) {
        log.info("Deleting trainee with username: {}", username);

        traineeService.deleteTraineeByUsername(username);
    }

    @Authenticated
    public TraineeResponseDto getTraineeByUsername(String username) {
        log.info("Retrieving trainee by username: {}", username);
        Trainee trainee = traineeService.getByUsername(username);

        return traineeMapper.toDto(trainee);
    }

    public TrainerResponseDto createTrainer(TrainerCreateRequestDto trainerCreateRequestDto) {
        log.info("Creating trainer: {} {}", trainerCreateRequestDto.getFirstName(), trainerCreateRequestDto.getLastName());
        Trainer saved = trainerService.createTrainer(trainerCreateRequestDto);

        return trainerMapper.toDto(saved);
    }

    @Authenticated
    @CheckOwnProfile(usernameParam = "trainerUpdateRequestDto")
    public TrainerResponseDto updateTrainer(TrainerUpdateRequestDto trainerUpdateRequestDto) {
        log.info("Updating trainer with username: {}", trainerUpdateRequestDto.getUsername());
        Trainer updated = trainerService.updateTrainer(trainerUpdateRequestDto);

        return trainerMapper.toDto(updated);
    }

    @Authenticated
    public TrainerResponseDto getTrainerByUsername(String username) {
        log.info("Retrieving trainer by username: {}", username);
        Trainer trainer = trainerService.getByUsername(username);

        return trainerMapper.toDto(trainer);
    }

    @Authenticated
    public TrainingResponseDto createTraining(TrainingCreateRequestDto trainingCreateRequestDto) {
        log.info("Creating training: {}", trainingCreateRequestDto.getName());
        Training saved = trainingService.createTraining(trainingCreateRequestDto);

        return trainingMapper.toDto(saved);
    }

    @Authenticated
    public TrainingResponseDto getTraining(Long id) {
        log.info("Retrieving training with id: {}", id);
        Training training = trainingService.getTraining(id);

        return trainingMapper.toDto(training);
    }

    @Authenticated
    public void changePassword(PasswordChangeRequestDto dto) {
        log.info("Changing password for username: {}", dto.getUsername());

        userService.changePassword(dto);
    }

    public AuthResponseDto authenticate(AuthRequestDto dto) {
        log.info("Authenticating user: {}", dto.getUsername());

        return authService.authenticate(dto);
    }
}
