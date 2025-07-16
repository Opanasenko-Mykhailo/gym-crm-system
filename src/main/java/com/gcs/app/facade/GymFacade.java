package com.gcs.app.facade;

import com.gcs.app.rest.AuthResponse;
import com.gcs.app.rest.TrainerProfileResponse;
import com.gcs.app.rest.TrainerRegistrationRequest;
import com.gcs.app.rest.TrainerUpdateRequest;
import com.gcs.app.rest.TrainingResponse;
import com.gcs.app.facade.dto.AuthRequestDto;
import com.gcs.app.facade.dto.AuthResponseDto;
import com.gcs.app.facade.dto.PasswordChangeRequestDto;
import com.gcs.app.facade.dto.TraineeCreateRequestDto;
import com.gcs.app.facade.dto.TraineeResponseDto;
import com.gcs.app.facade.dto.TraineeTrainingSearchCriteriaDto;
import com.gcs.app.facade.dto.TraineeUpdateRequestDto;
import com.gcs.app.facade.dto.TrainerCreateRequestDto;
import com.gcs.app.facade.dto.TrainerResponseDto;
import com.gcs.app.facade.dto.TrainerTrainingSearchCriteriaDto;
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
import com.gcs.app.security.MatchEntityOwner;
import com.gcs.app.service.TraineeService;
import com.gcs.app.service.TrainerService;
import com.gcs.app.service.TrainingService;
import com.gcs.app.service.UserService;
import com.gcs.app.service.common.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

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
    @MatchEntityOwner(usernameParam = "traineeUpdateRequestDto")
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

    @Authenticated
    public List<TrainingResponseDto> getTraineeTrainings(TraineeTrainingSearchCriteriaDto criteria) {
        log.info("Getting trainee trainings with criteria: {}", criteria);
        var trainings = traineeService.getTraineeTrainings(criteria);

        return trainings.stream()
                .map(trainingMapper::toDto)
                .toList();
    }

    @Authenticated
    @MatchEntityOwner(usernameParam = "traineeUsername")
    public TraineeResponseDto updateTraineeTrainers(String traineeUsername, List<String> trainerUsernames) {
        log.info("Updating trainers for trainee: {}", traineeUsername);
        Trainee updated = traineeService.updateTraineeTrainers(traineeUsername, trainerUsernames);

        return traineeMapper.toDto(updated);
    }

    @Authenticated
    public void setTraineeActive(String username, boolean isActive) {
        log.info("Setting trainee {} to {}", username, isActive ? "active" : "inactive");

        traineeService.setTraineeActivationStatus(username, isActive);
    }

    public AuthResponse createTrainer(TrainerRegistrationRequest swaggerRequest) {
        log.info("Creating trainer: {} {}", swaggerRequest.getFirstName(), swaggerRequest.getLastName());
        TrainerCreateRequestDto createRequestDto = trainerMapper.toCreateRequestDto(swaggerRequest);

        Trainer saved = trainerService.createTrainer(createRequestDto);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setUsername(saved.getUser().getUsername());
        authResponse.setPassword(saved.getUser().getPassword());

        return authResponse;
    }

    @Authenticated
    @MatchEntityOwner(usernameParam = "username")
    public TrainerProfileResponse updateTrainer(TrainerUpdateRequest swaggerRequest, String username) {
        log.info("Updating trainer with username: {}", username);
        TrainerUpdateRequestDto updateRequestDto = trainerMapper.toUpdateRequestDto(swaggerRequest);

        return trainerMapper.toRestModel(trainerService.updateTrainer(updateRequestDto));
    }

    @Authenticated
    public TrainerProfileResponse getTrainerByUsername(String username) {
        log.info("Retrieving trainer by username: {}", username);
        Trainer trainer = trainerService.getByUsername(username);
        return trainerMapper.toRestModel(trainer);
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
    public List<TrainingResponse> getTrainerTrainings(TrainerTrainingSearchCriteriaDto criteria) {
        log.info("Getting trainer trainings with criteria: {}", criteria);
        var trainings = trainerService.getTrainerTrainings(criteria);

        return trainings.stream()
                .map(trainingMapper::toDto)
                .map(trainingMapper::toRestModel)
                .toList();
    }

    @Authenticated
    public void setTrainerActive(String username, boolean isActive) {
        log.info("Setting trainer {} to {}", username, isActive ? "active" : "inactive");

        trainerService.setTrainerActivationStatus(username, isActive);
    }

    @Authenticated
    public List<TrainerResponseDto> getUnassignedTrainers(String traineeUsername) {
        log.info("Getting unassigned trainers for trainee: {}", traineeUsername);
        List<Trainer> trainers = traineeService.getUnassignedTrainers(traineeUsername);

        return trainers.stream()
                .map(trainerMapper::toDto)
                .toList();
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
