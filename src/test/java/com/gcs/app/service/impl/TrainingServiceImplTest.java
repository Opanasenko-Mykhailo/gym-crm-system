package com.gcs.app.service.impl;

import com.gcs.app.dao.TrainingDao;
import com.gcs.app.exception.ServiceException;
import com.gcs.app.facade.dto.TrainingCreateRequestDto;
import com.gcs.app.mapper.TrainingMapper;
import com.gcs.app.model.Trainee;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.Training;
import com.gcs.app.model.TrainingType;
import com.gcs.app.model.User;
import com.gcs.app.service.TraineeService;
import com.gcs.app.service.TrainerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    private static final String TRAINEE_USERNAME = "trainee.user";
    private static final String TRAINER_USERNAME = "trainer.user";
    private static final String NAME = "Yoga Session";
    private static final String TYPE = "Yoga";
    private static final LocalDate DATE = LocalDate.of(2025, 6, 30);
    private static final Long DURATION = 60L;

    private final Training expected = createTraining();
    private final TrainingCreateRequestDto createRequestDto = createTrainingCreateRequestDto();

    @Mock
    private TrainingDao trainingDao;

    @Mock
    private TrainingMapper trainingMapper;

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @InjectMocks
    private TrainingServiceImpl service;

    @Test
    void createTraining_mapsDtoAndCreatesTraining_returnsTraining() {
        when(trainingMapper.toEntity(createRequestDto)).thenReturn(expected);
        when(traineeService.getByUsername(TRAINEE_USERNAME)).thenReturn(createTrainee());
        when(trainerService.getByUsername(TRAINER_USERNAME)).thenReturn(createTrainer());
        when(trainingDao.create(expected)).thenReturn(expected);

        Training actual = service.createTraining(createRequestDto);

        assertEquals(TRAINEE_USERNAME, actual.getTrainee().getUser().getUsername());
        assertEquals(TRAINER_USERNAME, actual.getTrainer().getUser().getUsername());
        assertEquals(NAME, actual.getName());
        assertEquals(TYPE, actual.getType().getName());
        assertEquals(DATE, actual.getDate());
        assertEquals(DURATION, actual.getDuration());

        verify(trainingMapper).toEntity(createRequestDto);
        verify(traineeService).getByUsername(TRAINEE_USERNAME);
        verify(trainerService).getByUsername(TRAINER_USERNAME);
        verify(trainingDao).create(expected);
    }

    @Test
    void createTraining_whenTraineeDoesNotExist_throwsServiceException() {
        when(trainingMapper.toEntity(createRequestDto)).thenReturn(expected);
        when(traineeService.getByUsername(TRAINEE_USERNAME)).thenReturn(null);

        ServiceException ex = assertThrows(ServiceException.class, () -> service.createTraining(createRequestDto));

        assertEquals("Trainee with username " + TRAINEE_USERNAME + " not found", ex.getMessage());
        verify(trainingMapper).toEntity(createRequestDto);
        verify(traineeService).getByUsername(TRAINEE_USERNAME);
    }

    @Test
    void createTraining_whenTrainerDoesNotExist_throwsServiceException() {
        when(trainingMapper.toEntity(createRequestDto)).thenReturn(expected);
        when(traineeService.getByUsername(TRAINEE_USERNAME)).thenReturn(createTrainee());
        when(trainerService.getByUsername(TRAINER_USERNAME)).thenReturn(null);

        ServiceException ex = assertThrows(ServiceException.class, () -> service.createTraining(createRequestDto));

        assertEquals("Trainer with id " + TRAINER_USERNAME + " not found", ex.getMessage());
        verify(trainingMapper).toEntity(createRequestDto);
        verify(traineeService).getByUsername(TRAINEE_USERNAME);
        verify(trainerService).getByUsername(TRAINER_USERNAME);
    }

    @Test
    void getTraining_whenTrainingExists_returnsTraining() {
        when(trainingDao.get(1L)).thenReturn(Optional.of(expected));

        Training actual = service.getTraining(1L);

        assertEquals(TRAINEE_USERNAME, actual.getTrainee().getUser().getUsername());
        assertEquals(TRAINER_USERNAME, actual.getTrainer().getUser().getUsername());
        assertEquals(NAME, actual.getName());
        assertEquals(TYPE, actual.getType().getName());
        assertEquals(DATE, actual.getDate());
        assertEquals(DURATION, actual.getDuration());

        verify(trainingDao).get(1L);
    }

    @Test
    void getTraining_whenTrainingDoesNotExist_throwsServiceException() {
        when(trainingDao.get(1L)).thenReturn(Optional.empty());

        ServiceException ex = assertThrows(ServiceException.class, () -> service.getTraining(1L));
        assertEquals("Training with id 1 not found", ex.getMessage());
        verify(trainingDao).get(1L);
    }

    private Training createTraining() {
        return Training.builder()
                .trainee(createTrainee())
                .trainer(createTrainer())
                .name(NAME)
                .type(createTrainingType())
                .date(DATE)
                .duration(DURATION)
                .build();
    }

    private Trainee createTrainee() {
        return Trainee.builder()
                .user(createUser(TRAINEE_USERNAME))
                .build();
    }

    private Trainer createTrainer() {
        return Trainer.builder()
                .user(createUser(TRAINER_USERNAME))
                .build();
    }

    private User createUser(String username) {
        return User.builder()
                .username(username)
                .build();
    }

    private TrainingType createTrainingType() {
        return TrainingType.builder()
                .name(TYPE)
                .build();
    }

    private TrainingCreateRequestDto createTrainingCreateRequestDto() {
        TrainingCreateRequestDto dto = new TrainingCreateRequestDto();
        dto.setTraineeUsername(TRAINEE_USERNAME);
        dto.setTrainerUsername(TRAINER_USERNAME);
        dto.setName(NAME);
        dto.setType(createTrainingType());
        dto.setDate(DATE);
        dto.setDuration(DURATION);
        return dto;
    }
}