package com.gcs.app.dao.impl;

import com.gcs.app.model.Trainee;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.Training;
import com.gcs.app.model.TrainingType;
import org.hibernate.IdentifierLoadAccess;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

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
    private static final Long DURATION = 60L;

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private IdentifierLoadAccess<Training> identifierLoadAccess;

    @InjectMocks
    private TrainingDaoImpl dao;

    @Test
    void create_persistsTraining_returnsTraining() {
        Training training = createTraining();

        when(sessionFactory.getCurrentSession()).thenReturn(session);

        Training actual = dao.create(training);

        verify(session).persist(training);
        assertEquals(ID, actual.getId());
        assertEquals(TRAINEE_ID, actual.getTrainee().getId());
        assertEquals(TRAINER_ID, actual.getTrainer().getId());
        assertEquals(NAME, actual.getName());
        assertEquals(TYPE, actual.getType().getName());
        assertEquals(DATE, actual.getDate());
        assertEquals(DURATION, actual.getDuration());
    }

    @Test
    void get_whenTrainingExists_returnsTraining() {
        Training training = createTraining();

        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.byId(Training.class)).thenReturn(identifierLoadAccess);
        when(identifierLoadAccess.load(ID)).thenReturn(training);

        Optional<Training> actual = dao.get(ID);

        assertTrue(actual.isPresent());
        assertEquals(training, actual.get());
        assertEquals(TRAINEE_ID, actual.get().getTrainee().getId());
        assertEquals(TRAINER_ID, actual.get().getTrainer().getId());
        assertEquals(NAME, actual.get().getName());
        assertEquals(TYPE, actual.get().getType().getName());
        assertEquals(DATE, actual.get().getDate());
        assertEquals(DURATION, actual.get().getDuration());
    }

    @Test
    void get_whenTrainingDoesNotExist_returnsEmptyOptional() {
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.byId(Training.class)).thenReturn(identifierLoadAccess);
        when(identifierLoadAccess.load(ID)).thenReturn(null);

        Optional<Training> actual = dao.get(ID);

        assertFalse(actual.isPresent());
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
}
