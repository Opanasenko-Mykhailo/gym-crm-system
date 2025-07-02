package com.gcs.app.dao.impl;

import com.gcs.app.model.Trainee;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.Training;
import com.gcs.app.model.TrainingType;
import com.gcs.app.storage.InMemoryStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;

import static com.gcs.app.model.enums.EntityType.TRAINING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingDaoImplTest {

    private static final Long ID = 1L;
    private static final Long TRAINEE_ID = 2L;
    private static final Long TRAINER_ID = 3L;
    private static final String NAME = "Yoga Session";
    private static final String TYPE = "Yoga";
    private static final LocalDate DATE = LocalDate.of(2025, 6, 30);
    private static final Duration DURATION = Duration.ofHours(1);

    @Mock
    private InMemoryStorage storage;

    @InjectMocks
    private TrainingDaoImpl dao;

    private Training expected;

    @BeforeEach
    void setUp() {
        expected = buildTraining();
    }

    @Test
    void create_assignsIdAndStoresTraining_returnsTraining() {
        when(storage.nextId()).thenReturn(ID);

        Training actual = dao.create(expected);

        assertEquals(ID, actual.getId());
        assertEquals(TRAINEE_ID, actual.getTrainee().getId());
        assertEquals(TRAINER_ID, actual.getTrainer().getId());
        assertEquals(NAME, actual.getName());
        assertEquals(TYPE, actual.getType().getName());
        assertEquals(DATE, actual.getDate());
        assertEquals(DURATION, actual.getDuration());

        verify(storage).nextId();
        verify(storage).put(TRAINING, ID, expected);
    }

    @Test
    void get_whenTrainingExists_returnsTraining() {
        when(storage.getById(TRAINING, ID)).thenReturn(Optional.of(expected));

        Optional<Training> actual = dao.get(ID);

        assertTrue(actual.isPresent());
        assertEquals(TRAINEE_ID, actual.get().getTrainee().getId());
        assertEquals(TRAINER_ID, actual.get().getTrainer().getId());
        assertEquals(NAME, actual.get().getName());
        assertEquals(TYPE, actual.get().getType().getName());
        assertEquals(DATE, actual.get().getDate());
        assertEquals(DURATION, actual.get().getDuration());

        verify(storage).getById(TRAINING, ID);
    }

    @Test
    void get_whenTrainingDoesNotExist_returnsEmptyOptional() {
        when(storage.getById(TRAINING, ID)).thenReturn(Optional.empty());

        Optional<Training> actual = dao.get(ID);

        assertFalse(actual.isPresent());
        verify(storage).getById(TRAINING, ID);
    }

    private Training buildTraining() {
        return Training.builder()
                .id(ID)
                .trainee(Trainee.builder()
                        .id(TRAINEE_ID)
                        .build())
                .trainer(Trainer.builder()
                        .id(TRAINER_ID).build())
                .name(NAME)
                .type(TrainingType.builder()
                        .name(TYPE)
                        .build())
                .date(DATE)
                .duration(DURATION)
                .build();
    }
}