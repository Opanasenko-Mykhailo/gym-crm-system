package com.gcs.app.dao.impl;

import com.gcs.app.dao.TestRepository;
import com.gcs.app.model.Trainee;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.Training;
import com.gcs.app.model.TrainingType;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


class TrainingDaoImplTest extends TestRepository<TrainingDaoImpl> {

    private static final Long EXISTING_TRAINING_ID = 3L;
    private static final String EXISTING_TRAINING_NAME = "Morning Yoga";
    private static final LocalDate EXISTING_TRAINING_DATE = LocalDate.of(2025, 10, 1);
    private static final Long EXISTING_TRAINING_DURATION = 60L;

    private static final Long EXISTING_TRAINEE_ID = 1L;
    private static final Long EXISTING_TRAINER_ID = 1L;
    private static final Long EXISTING_TRAINING_TYPE_ID = 1L;
    private static final String EXISTING_TRAINING_TYPE_NAME = "Yoga";

    @Override
    protected TrainingDaoImpl initDao() {
        return new TrainingDaoImpl(sessionFactory);
    }

    @Override
    protected String getXmlDataPath() {
        return "/dbunit/training-data.xml";
    }

    @Test
    void create_persistsTraining_returnsTraining() {
        Session session = sessionFactory.getCurrentSession();

        Trainee existingTrainee = session.find(Trainee.class, EXISTING_TRAINEE_ID);
        Trainer existingTrainer = session.find(Trainer.class, EXISTING_TRAINER_ID);
        TrainingType existingType = session.find(TrainingType.class, EXISTING_TRAINING_TYPE_ID);


        Training newTraining = Training.builder()
                .trainee(existingTrainee)
                .trainer(existingTrainer)
                .type(existingType)
                .name("New Test Training")
                .date(LocalDate.of(2026, 7, 7))
                .duration(75L)
                .build();

        dao.create(newTraining);
        session.getTransaction().commit();


        try (Session verifySession = sessionFactory.openSession()) {
            Training saved = verifySession.find(Training.class, newTraining.getId());

            assertNotNull(saved);
            assertEquals("New Test Training", saved.getName());
            assertEquals(LocalDate.of(2026, 7, 7), saved.getDate());
            assertEquals(75L, saved.getDuration());
            assertEquals(existingType.getName(), saved.getType().getName());
            assertEquals(existingTrainee.getId(), saved.getTrainee().getId());
            assertEquals(existingTrainer.getId(), saved.getTrainer().getId());
        }
    }


    @Test
    void get_whenTrainingExists_returnsTraining() {
        Session session = sessionFactory.getCurrentSession();
        Optional<Training> optionalTraining = dao.get(EXISTING_TRAINING_ID);


        assertTrue(optionalTraining.isPresent());
        Training result = optionalTraining.get();

        assertEquals(EXISTING_TRAINING_ID, result.getId());
        assertEquals(EXISTING_TRAINING_NAME, result.getName());
        assertEquals(EXISTING_TRAINING_DATE, result.getDate());
        assertEquals(EXISTING_TRAINING_DURATION, result.getDuration());
        assertEquals(EXISTING_TRAINING_TYPE_NAME, result.getType().getName());
        assertEquals(EXISTING_TRAINEE_ID, result.getTrainee().getId());
        assertEquals(EXISTING_TRAINER_ID, result.getTrainer().getId());

        session.getTransaction().commit();
    }

    @Test
    void get_whenTrainingDoesNotExist_returnsEmptyOptional() {
        Session session = sessionFactory.getCurrentSession();
        Optional<Training> result = dao.get(999L);

        assertFalse(result.isPresent());

        session.getTransaction().commit();
    }
}