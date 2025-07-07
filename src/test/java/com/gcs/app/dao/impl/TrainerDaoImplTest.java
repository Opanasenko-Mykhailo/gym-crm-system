package com.gcs.app.dao.impl;

import com.gcs.app.dao.TestRepository;
import com.gcs.app.exception.EntityNotFoundException;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.TrainingType;
import com.gcs.app.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class TrainerDaoImplTest extends TestRepository<TrainerDaoImpl> {

    private static final Long NON_EXISTENT_ID = 999L;
    private static final String FIRST_NAME = "Jane";
    private static final String LAST_NAME = "Smith";
    private static final String USERNAME = "jane.smith";
    private static final String PASSWORD = "password";
    private static final String SPECIALIZATION = "Yoga";

    @Override
    protected TrainerDaoImpl initDao() {
        return new TrainerDaoImpl(sessionFactory);
    }

    @Override
    protected String getXmlDataPath() {
        return "/dbunit/trainer-data.xml";
    }

    @Test
    void get_whenTrainerExists_returnsTrainer() {
        Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();
        Optional<Trainer> result = dao.get(1L);
        transaction.commit();

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void get_whenTrainerDoesNotExist_returnsEmptyOptional() {
        Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();
        Optional<Trainer> result = dao.get(NON_EXISTENT_ID);
        transaction.commit();

        assertFalse(result.isPresent());
    }

    @Test
    void update_whenTrainerExists_mergesAndReturnsTrainer() {
        Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();
        Trainer existing = dao.get(1L).orElseThrow();
        Trainer updated = existing.toBuilder()
                .user(existing.getUser().toBuilder().firstName("Anna").build())
                .build();

        Trainer result = dao.update(updated);
        transaction.commit();

        assertEquals("Anna", result.getUser().getFirstName());
    }

    @Test
    void update_whenTrainerDoesNotExist_throwsEntityNotFoundException() {
        Trainer ghost = Trainer.builder()
                .id(NON_EXISTENT_ID)
                .user(createUser())
                .specialization(createTrainingType())
                .build();

        Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> dao.update(ghost));

        transaction.rollback();

        assertEquals("Trainer with id 999 not found", ex.getMessage());
    }

    @Test
    void create_persistsTrainer() {
        Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();

        User existingUser = sessionFactory.getCurrentSession().find(User.class, 1L);
        TrainingType existingType = sessionFactory.getCurrentSession().find(TrainingType.class, 1L);

        Trainer newTrainer = Trainer.builder()
                .user(existingUser)
                .specialization(existingType)
                .build();

        dao.create(newTrainer);
        transaction.commit();

        try (Session verifySession = sessionFactory.openSession()) {
            Trainer saved = verifySession.find(Trainer.class, newTrainer.getId());

            assertNotNull(saved);
            assertEquals(existingUser.getUsername(), saved.getUser().getUsername());
        }
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
