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
import com.gcs.app.model.TrainingType;
import com.gcs.app.model.User;
import com.gcs.app.service.TraineeService;
import com.gcs.app.service.TrainerService;
import com.gcs.app.service.TrainingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GymFacadeTest {

    private static final Long TRAINEE_ID = 1L;
    private static final String TRAINEE_FIRST_NAME = "John";
    private static final String TRAINEE_LAST_NAME = "Doe";
    private static final String TRAINEE_USERNAME = "john.doe";
    private static final LocalDate TRAINEE_DATE_OF_BIRTH = LocalDate.of(1990, 1, 1);
    private static final String TRAINEE_ADDRESS = "123 Main St";

    private static final Long TRAINER_ID = 2L;
    private static final String TRAINER_FIRST_NAME = "Jane";
    private static final String TRAINER_LAST_NAME = "Smith";
    private static final String TRAINER_USERNAME = "jane.smith";
    private static final String SPECIALIZATION = "Yoga";

    private static final Long TRAINING_ID = 1L;
    private static final String TRAINING_NAME = "Yoga Session";
    private static final LocalDate TRAINING_DATE = LocalDate.of(2025, 6, 30);
    private static final Long TRAINING_DURATION = 60L;

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    @Mock
    private TraineeMapper traineeMapper;

    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private TrainingMapper trainingMapper;

    @InjectMocks
    private GymFacade facade;

    private Trainee trainee = createTrainee();
    private Trainer trainer = createTrainer();
    private Training training = createTraining();
    private TraineeCreateRequestDto traineeCreateRequestDto = createTraineeCreateRequestDto();
    private TraineeUpdateRequestDto traineeUpdateRequestDto = createTraineeUpdateRequestDto();
    private TraineeResponseDto expectedTraineeResponse = createTraineeResponseDto();
    private TrainerCreateRequestDto trainerCreateRequestDto = createTrainerCreateRequestDto();
    private TrainerUpdateRequestDto trainerUpdateRequestDto = createTrainerUpdateRequestDto();
    private TrainerResponseDto expectedTrainerResponse = createTrainerResponseDto();
    private TrainingCreateRequestDto trainingCreateRequestDto = createTrainingCreateRequestDto();
    private TrainingResponseDto expectedTrainingResponse = createTrainingResponseDto();

    @Test
    void createTrainee_callsServiceAndMapper_returnsTraineeResponseDto() {
        when(traineeService.createTrainee(traineeCreateRequestDto)).thenReturn(trainee);
        when(traineeMapper.toDto(trainee)).thenReturn(expectedTraineeResponse);

        TraineeResponseDto actual = facade.createTrainee(traineeCreateRequestDto);

        assertEquals(TRAINEE_ID, actual.getUserId());
        assertEquals(TRAINEE_FIRST_NAME, actual.getFirstName());
        assertEquals(TRAINEE_LAST_NAME, actual.getLastName());
        assertEquals(TRAINEE_USERNAME, actual.getUsername());
        assertTrue(actual.getIsActive());
        assertEquals(TRAINEE_DATE_OF_BIRTH, actual.getDateOfBirth());
        assertEquals(TRAINEE_ADDRESS, actual.getAddress());

        verify(traineeService).createTrainee(traineeCreateRequestDto);
        verify(traineeMapper).toDto(trainee);
    }

    @Test
    void updateTrainee_callsServiceAndMapper_returnsTraineeResponseDto() {
        when(traineeService.updateTrainee(traineeUpdateRequestDto)).thenReturn(trainee);
        when(traineeMapper.toDto(trainee)).thenReturn(expectedTraineeResponse);

        TraineeResponseDto actual = facade.updateTrainee(traineeUpdateRequestDto);

        assertEquals(TRAINEE_ID, actual.getUserId());
        assertEquals(TRAINEE_FIRST_NAME, actual.getFirstName());
        assertEquals(TRAINEE_LAST_NAME, actual.getLastName());
        assertEquals(TRAINEE_USERNAME, actual.getUsername());
        assertTrue(actual.getIsActive());
        assertEquals(TRAINEE_DATE_OF_BIRTH, actual.getDateOfBirth());
        assertEquals(TRAINEE_ADDRESS, actual.getAddress());

        verify(traineeService).updateTrainee(traineeUpdateRequestDto);
        verify(traineeMapper).toDto(trainee);
    }

    @Test
    void deleteTraineeByUsername_callsService() {
        facade.deleteTraineeByUsername(TRAINEE_USERNAME);

        verify(traineeService).deleteTraineeByUsername(TRAINEE_USERNAME);
    }

    @Test
    void getTraineeByUsername_callsServiceAndMapper_returnsTraineeResponseDto() {
        when(traineeService.getByUsername(TRAINEE_USERNAME)).thenReturn(trainee);
        when(traineeMapper.toDto(trainee)).thenReturn(expectedTraineeResponse);

        TraineeResponseDto actual = facade.getTraineeByUsername(TRAINEE_USERNAME);

        assertEquals(TRAINEE_ID, actual.getUserId());
        assertEquals(TRAINEE_FIRST_NAME, actual.getFirstName());
        assertEquals(TRAINEE_LAST_NAME, actual.getLastName());
        assertEquals(TRAINEE_USERNAME, actual.getUsername());

        verify(traineeService).getByUsername(TRAINEE_USERNAME);
        verify(traineeMapper).toDto(trainee);
    }

    @Test
    void changeTraineePassword_callsService() {
        PasswordChangeRequestDto dto = new PasswordChangeRequestDto();
        dto.setUsername(TRAINEE_USERNAME);
        dto.setOldPassword("oldPass123");
        dto.setNewPassword("newPass123!");

        facade.changeTraineePassword(dto);

        verify(traineeService).changePassword(dto);
    }

    @Test
    void createTrainer_callsServiceAndMapper_returnsTrainerResponseDto() {
        when(trainerService.createTrainer(trainerCreateRequestDto)).thenReturn(trainer);
        when(trainerMapper.toDto(trainer)).thenReturn(expectedTrainerResponse);

        TrainerResponseDto actual = facade.createTrainer(trainerCreateRequestDto);

        assertEquals(TRAINER_ID, actual.getUserId());
        assertEquals(TRAINER_FIRST_NAME, actual.getFirstName());
        assertEquals(TRAINER_LAST_NAME, actual.getLastName());
        assertEquals(TRAINER_USERNAME, actual.getUsername());
        assertTrue(actual.getIsActive());
        assertEquals(SPECIALIZATION, actual.getSpecialization().getName());

        verify(trainerService).createTrainer(trainerCreateRequestDto);
        verify(trainerMapper).toDto(trainer);
    }

    @Test
    void updateTrainer_callsServiceAndMapper_returnsTrainerResponseDto() {
        when(trainerService.updateTrainer(trainerUpdateRequestDto)).thenReturn(trainer);
        when(trainerMapper.toDto(trainer)).thenReturn(expectedTrainerResponse);

        TrainerResponseDto actual = facade.updateTrainer(trainerUpdateRequestDto);

        assertEquals(TRAINER_ID, actual.getUserId());
        assertEquals(TRAINER_FIRST_NAME, actual.getFirstName());
        assertEquals(TRAINER_LAST_NAME, actual.getLastName());
        assertEquals(TRAINER_USERNAME, actual.getUsername());
        assertTrue(actual.getIsActive());
        assertEquals(SPECIALIZATION, actual.getSpecialization().getName());

        verify(trainerService).updateTrainer(trainerUpdateRequestDto);
        verify(trainerMapper).toDto(trainer);
    }

    @Test
    void getTrainerByUsername_callsServiceAndMapper_returnsTrainerResponseDto() {
        when(trainerService.getByUsername(TRAINER_USERNAME)).thenReturn(trainer);
        when(trainerMapper.toDto(trainer)).thenReturn(expectedTrainerResponse);

        TrainerResponseDto actual = facade.getTrainerByUsername(TRAINER_USERNAME);

        assertEquals(TRAINER_ID, actual.getUserId());
        assertEquals(TRAINER_FIRST_NAME, actual.getFirstName());
        assertEquals(TRAINER_LAST_NAME, actual.getLastName());
        assertEquals(TRAINER_USERNAME, actual.getUsername());

        verify(trainerService).getByUsername(TRAINER_USERNAME);
        verify(trainerMapper).toDto(trainer);
    }

    @Test
    void changeTrainerPassword_callsService() {
        PasswordChangeRequestDto dto = new PasswordChangeRequestDto();
        dto.setUsername(TRAINER_USERNAME);
        dto.setOldPassword("oldPass123");
        dto.setNewPassword("newPass123!");

        facade.changeTrainerPassword(dto);

        verify(trainerService).changePassword(dto);
    }

    @Test
    void createTraining_callsServiceAndMapper_returnsTrainingResponseDto() {
        when(trainingService.createTraining(trainingCreateRequestDto)).thenReturn(training);
        when(trainingMapper.toDto(training)).thenReturn(expectedTrainingResponse);

        TrainingResponseDto actual = facade.createTraining(trainingCreateRequestDto);

        assertEquals(TRAINING_ID, actual.getId());
        assertEquals(TRAINEE_ID, actual.getTraineeId());
        assertEquals(TRAINER_ID, actual.getTrainerId());
        assertEquals(TRAINING_NAME, actual.getName());
        assertEquals(SPECIALIZATION, actual.getType().getName());
        assertEquals(TRAINING_DATE, actual.getDate());
        assertEquals(TRAINING_DURATION, actual.getDuration());

        verify(trainingService).createTraining(trainingCreateRequestDto);
        verify(trainingMapper).toDto(training);
    }

    @Test
    void getTraining_callsServiceAndMapper_returnsTrainingResponseDto() {
        when(trainingService.getTraining(TRAINING_ID)).thenReturn(training);
        when(trainingMapper.toDto(training)).thenReturn(expectedTrainingResponse);

        TrainingResponseDto actual = facade.getTraining(TRAINING_ID);

        assertEquals(TRAINING_ID, actual.getId());
        assertEquals(TRAINEE_ID, actual.getTraineeId());
        assertEquals(TRAINER_ID, actual.getTrainerId());
        assertEquals(TRAINING_NAME, actual.getName());
        assertEquals(SPECIALIZATION, actual.getType().getName());
        assertEquals(TRAINING_DATE, actual.getDate());
        assertEquals(TRAINING_DURATION, actual.getDuration());

        verify(trainingService).getTraining(TRAINING_ID);
        verify(trainingMapper).toDto(training);
    }

    private Trainee createTrainee() {
        return Trainee.builder()
                .id(TRAINEE_ID)
                .user(createTraineeUser())
                .dateOfBirth(TRAINEE_DATE_OF_BIRTH)
                .address(TRAINEE_ADDRESS)
                .build();
    }

    private User createTraineeUser() {
        return User.builder()
                .firstName(TRAINEE_FIRST_NAME)
                .lastName(TRAINEE_LAST_NAME)
                .username(TRAINEE_USERNAME)
                .isActive(true)
                .build();
    }

    private Trainer createTrainer() {
        return Trainer.builder()
                .id(TRAINER_ID)
                .user(createTrainerUser())
                .specialization(createTrainingType())
                .build();
    }

    private User createTrainerUser() {
        return User.builder()
                .firstName(TRAINER_FIRST_NAME)
                .lastName(TRAINER_LAST_NAME)
                .username(TRAINER_USERNAME)
                .isActive(true)
                .build();
    }

    private Training createTraining() {
        return Training.builder()
                .id(TRAINING_ID)
                .trainee(createTraineeForTraining())
                .trainer(createTrainerForTraining())
                .name(TRAINING_NAME)
                .type(createTrainingType())
                .date(TRAINING_DATE)
                .duration(TRAINING_DURATION)
                .build();
    }

    private Trainee createTraineeForTraining() {
        return Trainee.builder()
                .id(TRAINEE_ID)
                .build();
    }

    private Trainer createTrainerForTraining() {
        return Trainer.builder()
                .id(TRAINER_ID)
                .build();
    }

    private TrainingType createTrainingType() {
        return TrainingType.builder()
                .name(SPECIALIZATION)
                .build();
    }

    private TraineeCreateRequestDto createTraineeCreateRequestDto() {
        TraineeCreateRequestDto dto = new TraineeCreateRequestDto();
        dto.setFirstName(TRAINEE_FIRST_NAME);
        dto.setLastName(TRAINEE_LAST_NAME);
        dto.setDateOfBirth(TRAINEE_DATE_OF_BIRTH);
        dto.setAddress(TRAINEE_ADDRESS);
        return dto;
    }

    private TraineeUpdateRequestDto createTraineeUpdateRequestDto() {
        TraineeUpdateRequestDto dto = new TraineeUpdateRequestDto();
        dto.setUsername(TRAINEE_USERNAME);
        dto.setFirstName(TRAINEE_FIRST_NAME);
        dto.setLastName(TRAINEE_LAST_NAME);
        dto.setDateOfBirth(TRAINEE_DATE_OF_BIRTH);
        dto.setAddress(TRAINEE_ADDRESS);
        dto.setIsActive(true);
        return dto;
    }

    private TraineeResponseDto createTraineeResponseDto() {
        TraineeResponseDto dto = new TraineeResponseDto();
        dto.setUserId(TRAINEE_ID);
        dto.setFirstName(TRAINEE_FIRST_NAME);
        dto.setLastName(TRAINEE_LAST_NAME);
        dto.setUsername(TRAINEE_USERNAME);
        dto.setIsActive(true);
        dto.setDateOfBirth(TRAINEE_DATE_OF_BIRTH);
        dto.setAddress(TRAINEE_ADDRESS);
        return dto;
    }

    private TrainerCreateRequestDto createTrainerCreateRequestDto() {
        TrainerCreateRequestDto dto = new TrainerCreateRequestDto();
        dto.setFirstName(TRAINER_FIRST_NAME);
        dto.setLastName(TRAINER_LAST_NAME);
        dto.setSpecialization(createTrainingType());
        return dto;
    }

    private TrainerUpdateRequestDto createTrainerUpdateRequestDto() {
        TrainerUpdateRequestDto dto = new TrainerUpdateRequestDto();
        dto.setUsername(TRAINER_USERNAME);
        dto.setFirstName(TRAINER_FIRST_NAME);
        dto.setLastName(TRAINER_LAST_NAME);
        dto.setSpecialization(createTrainingType());
        dto.setIsActive(true);
        return dto;
    }

    private TrainerResponseDto createTrainerResponseDto() {
        TrainerResponseDto dto = new TrainerResponseDto();
        dto.setUserId(TRAINER_ID);
        dto.setFirstName(TRAINER_FIRST_NAME);
        dto.setLastName(TRAINER_LAST_NAME);
        dto.setUsername(TRAINER_USERNAME);
        dto.setIsActive(true);
        dto.setSpecialization(createTrainingType());
        return dto;
    }

    private TrainingCreateRequestDto createTrainingCreateRequestDto() {
        TrainingCreateRequestDto dto = new TrainingCreateRequestDto();
        dto.setTrainerUsername(TRAINEE_USERNAME);
        dto.setTrainerUsername(TRAINER_USERNAME);
        dto.setName(TRAINING_NAME);
        dto.setType(createTrainingType());
        dto.setDate(TRAINING_DATE);
        dto.setDuration(TRAINING_DURATION);
        return dto;
    }

    private TrainingResponseDto createTrainingResponseDto() {
        TrainingResponseDto dto = new TrainingResponseDto();
        dto.setId(TRAINING_ID);
        dto.setTraineeId(TRAINEE_ID);
        dto.setTrainerId(TRAINER_ID);
        dto.setName(TRAINING_NAME);
        dto.setType(createTrainingType());
        dto.setDate(TRAINING_DATE);
        dto.setDuration(TRAINING_DURATION);
        return dto;
    }
}
