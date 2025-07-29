package com.gcs.app.repository.impl;

import com.gcs.app.repository.AbstractRepositoryTest;
import com.gcs.app.model.Trainee;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.Training;
import com.gcs.app.model.TrainingType;
import com.github.database.rider.core.api.dataset.DataSet;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataSet(value = "dataset/training-data.xml", cleanBefore = true, cleanAfter = true, transactional = true, disableConstraints = true)
class TrainingDaoImplTest extends AbstractRepositoryTest<TrainingDaoImpl> {

    private static final Long EXISTING_TRAINING_ID = 4L;
    private static final String EXISTING_TRAINING_NAME = "Morning Yoga";
    private static final LocalDate EXISTING_TRAINING_DATE = LocalDate.of(2025, 10, 1);
    private static final Long EXISTING_TRAINING_DURATION = 60L;

    private static final Long EXISTING_TRAINEE_ID = 1L;
    private static final Long EXISTING_TRAINER_ID = 1L;
    private static final String EXISTING_TRAINING_TYPE_NAME = "Yoga";

    @Test
    void get_whenTrainingExists_returnsTraining() {
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
    }

    @Test
    void get_whenTrainingDoesNotExist_returnsEmptyOptional() {
        Optional<Training> result = dao.get(999L);
        assertFalse(result.isPresent());
    }

    @Test
    void create_persistsTraining() {
        Trainee existingTrainee = sessionFactory.getCurrentSession().find(Trainee.class, 2L);
        Trainer existingTrainer = sessionFactory.getCurrentSession().find(Trainer.class, 1L);
        TrainingType existingType = sessionFactory.getCurrentSession().find(TrainingType.class, 4L);

        assertNotNull(existingTrainee);
        assertNotNull(existingTrainer);
        assertNotNull(existingType);

        Training newTraining = Training.builder()
                .trainee(existingTrainee)
                .trainer(existingTrainer)
                .type(existingType)
                .name("CrossFit Burn")
                .date(LocalDate.of(2026, 7, 7))
                .duration(50L)
                .build();

        dao.create(newTraining);
        sessionFactory.getCurrentSession().flush();

        Training saved = sessionFactory.getCurrentSession().find(Training.class, newTraining.getId());

        assertNotNull(saved);
        assertEquals("CrossFit Burn", saved.getName());
        assertEquals(LocalDate.of(2026, 7, 7), saved.getDate());
        assertEquals(50L, saved.getDuration());
        assertEquals(existingTrainee.getId(), saved.getTrainee().getId());
        assertEquals(existingTrainer.getId(), saved.getTrainer().getId());
        assertEquals(existingType.getName(), saved.getType().getName());
    }
}
