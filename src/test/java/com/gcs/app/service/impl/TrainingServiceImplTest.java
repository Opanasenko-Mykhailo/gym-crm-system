package com.gcs.app.service.impl;

import com.gcs.app.dao.TrainingDao;
import com.gcs.app.exception.ServiceException;
import com.gcs.app.facade.dto.TrainingCreateRequestDto;
import com.gcs.app.mapper.TrainingMapper;
import com.gcs.app.model.Trainee;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.Training;
import com.gcs.app.model.TrainingType;
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

    private static final Long ID = 1L;
    private static final Long TRAINEE_ID = 2L;
    private static final Long TRAINER_ID = 3L;
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

    @InjectMocks
    private TrainingServiceImpl service;

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Test
    void createTraining_mapsDtoAndCreatesTraining_returnsTraining() {
        when(trainingMapper.toEntity(createRequestDto)).thenReturn(expected);
        when(trainingDao.create(expected)).thenReturn(expected);

        when(traineeService.getTrainee(TRAINEE_ID)).thenReturn(createTrainee());
        when(trainerService.getTrainer(TRAINER_ID)).thenReturn(createTrainer());

        Training actual = service.createTraining(createRequestDto);
        assertEquals(ID, actual.getId());
        assertEquals(TRAINEE_ID, actual.getTrainee().getId());
        assertEquals(TRAINER_ID, actual.getTrainer().getId());
        assertEquals(NAME, actual.getName());
        assertEquals(TYPE, actual.getType().getName());
        assertEquals(DATE, actual.getDate());
        assertEquals(DURATION, actual.getDuration());

        verify(trainingMapper).toEntity(createRequestDto);
        verify(trainingDao).create(expected);
    }

    @Test
    void getTraining_whenTrainingExists_returnsTraining() {
        when(trainingDao.get(ID)).thenReturn(Optional.of(expected));

        Training actual = service.getTraining(ID);
        assertEquals(ID, actual.getId());
        assertEquals(TRAINEE_ID, actual.getTrainee().getId());
        assertEquals(TRAINER_ID, actual.getTrainer().getId());
        assertEquals(NAME, actual.getName());
        assertEquals(TYPE, actual.getType().getName());
        assertEquals(DATE, actual.getDate());
        assertEquals(DURATION, actual.getDuration());

        verify(trainingDao).get(ID);
    }

    @Test
    void getTraining_whenTrainingDoesNotExist_throwsServiceException() {
        when(trainingDao.get(ID)).thenReturn(Optional.empty());

        ServiceException ex = assertThrows(ServiceException.class, () -> service.getTraining(ID));

        assertEquals("Training with id 1 not found", ex.getMessage());
        verify(trainingDao).get(ID);
    }

    private Training createTraining() {
        return Training.builder()
                .id(ID)
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
                .id(TRAINEE_ID)
                .build();
    }

    private Trainer createTrainer() {
        return Trainer.builder()
                .id(TRAINER_ID)
                .build();
    }

    private TrainingType createTrainingType() {
        return TrainingType.builder()
                .name(TYPE)
                .build();
    }

    private TrainingCreateRequestDto createTrainingCreateRequestDto() {
        TrainingCreateRequestDto dto = new TrainingCreateRequestDto();
        dto.setTraineeId(TRAINEE_ID);
        dto.setTrainerId(TRAINER_ID);
        dto.setName(NAME);
        dto.setType(createTrainingType());
        dto.setDate(DATE);
        dto.setDuration(DURATION);

        return dto;
    }
}