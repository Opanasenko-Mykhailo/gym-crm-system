package com.gcs.app.dao.impl;

import com.gcs.app.dao.AbstractRepositoryTest;
import com.gcs.app.exception.EntityNotFoundException;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.TrainingType;
import com.gcs.app.model.User;
import com.github.database.rider.core.api.dataset.DataSet;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataSet(value = "dataset/trainer-data.xml", cleanBefore = true, cleanAfter = true, transactional = true, disableConstraints = true)
class TrainerDaoImplTest extends AbstractRepositoryTest<TrainerDaoImpl> {

    private static final Long NON_EXISTENT_ID = 999L;
    private static final String FIRST_NAME = "Jane";
    private static final String LAST_NAME = "Smith";
    private static final String USERNAME = "jane.smith";
    private static final String PASSWORD = "password";
    private static final String SPECIALIZATION = "Yoga";

    @Test
    void findByUsername_whenTrainerExists_returnsTrainer() {
        Optional<Trainer> result = dao.findByUsername(USERNAME);

        assertTrue(result.isPresent());
        assertEquals(USERNAME, result.get().getUser().getUsername());
        assertEquals(FIRST_NAME, result.get().getUser().getFirstName());
    }

    @Test
    void findByUsername_whenTrainerDoesNotExist_returnsEmptyOptional() {
        Optional<Trainer> result = dao.findByUsername("non.existing.username");

        assertFalse(result.isPresent());
    }

    @Test
    void update_whenTrainerExists_mergesAndReturnsTrainer() {
        Optional<Trainer> existingOpt = dao.findByUsername(USERNAME);
        assertTrue(existingOpt.isPresent());
        Trainer existing = existingOpt.get();

        User updatedUser = existing.getUser().toBuilder()
                .firstName("Anna")
                .build();

        Trainer updated = existing.toBuilder()
                .user(updatedUser)
                .build();

        Trainer result = dao.update(updated);

        assertEquals("Anna", result.getUser().getFirstName());
        assertEquals(USERNAME, result.getUser().getUsername());
    }

    @Test
    void update_whenTrainerDoesNotExist_throwsEntityNotFoundException() {
        Trainer ghost = Trainer.builder()
                .id(NON_EXISTENT_ID)
                .user(createUser())
                .specialization(createTrainingType())
                .build();

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> dao.update(ghost));

        assertEquals("Trainer with id 999 not found", ex.getMessage());
    }

    @Test
    void create_persistsTrainer() {
        User existingUser = sessionFactory.getCurrentSession().find(User.class, 1L);
        TrainingType existingType = sessionFactory.getCurrentSession().find(TrainingType.class, 1L);

        assertNotNull(existingUser);
        assertNotNull(existingType);

        Trainer newTrainer = Trainer.builder()
                .user(existingUser)
                .specialization(existingType)
                .build();

        dao.create(newTrainer);
        sessionFactory.getCurrentSession().flush();

        Trainer saved = sessionFactory.getCurrentSession().find(Trainer.class, newTrainer.getId());

        assertNotNull(saved);
        assertEquals(existingUser.getUsername(), saved.getUser().getUsername());
        assertEquals(existingType.getName(), saved.getSpecialization().getName());
    }

    private User createUser() {
        return User.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .isActive(true)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .build();
    }

    private TrainingType createTrainingType() {
        return TrainingType.builder()
                .name(SPECIALIZATION)
                .build();
    }
}

