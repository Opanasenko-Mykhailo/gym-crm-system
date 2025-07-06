package com.gcs.app.dao.impl;

import com.gcs.app.config.TestConfig;
import com.gcs.app.exception.EntityNotFoundException;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.TrainingType;
import com.gcs.app.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
class TrainerDaoImplTest {

    private static final String FIRST_NAME = "Jane";
    private static final String LAST_NAME = "Smith";
    private static final String USERNAME = "jane.smith";
    private static final String PASSWORD = "password";
    private static final String SPECIALIZATION = "Yoga";

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private TrainerDaoImpl dao;

    @Test
    void create_persistsTrainer_returnsTrainer() {
        User user = createUser();
        TrainingType specialization = saveTrainingType(createTrainingType());
        Trainer trainer = createTrainer(user, specialization);

        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();

        try {
            dao.create(trainer);
            transaction.commit();
        } catch (Exception e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }

            fail("Error while creating trainer: " + e.getMessage());
        }

        try (Session verifySession = sessionFactory.openSession()) {
            Trainer saved = verifySession.find(Trainer.class, trainer.getId());

            assertNotNull(saved);
            assertEquals(USERNAME, saved.getUser().getUsername());
            assertEquals(SPECIALIZATION, saved.getSpecialization().getName());
        }
    }

    @Test
    void get_whenTrainerExists_returnsTrainer() {
        Trainer trainer = saveTrainer();

        Trainer result = null;
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();

        try {
            result = dao.get(trainer.getId()).orElse(null);
            transaction.commit();
        } catch (Exception e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }

            fail("Error while retrieving trainer: " + e.getMessage());
        }

        assertNotNull(result);
        assertEquals(trainer.getId(), result.getId());
    }

    @Test
    void get_whenTrainerDoesNotExist_returnsEmptyOptional() {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();
        Optional<Trainer> result = dao.get(999L);
        transaction.commit();

        assertFalse(result.isPresent());
    }

    @Test
    void update_whenTrainerExists_mergesAndReturnsTrainer() {
        Trainer existing = saveTrainer();
        Long id = existing.getId();

        User updatedUser = createUser("Anna");
        updatedUser = updatedUser.toBuilder()
                .username(USERNAME)
                .password(PASSWORD)
                .isActive(true)
                .lastName(LAST_NAME)
                .build();

        Trainer updatedTrainer = createTrainer(updatedUser, existing.getSpecialization())
                .toBuilder()
                .id(id)
                .build();

        Trainer result = null;
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();

        try {
            result = dao.update(updatedTrainer);
            transaction.commit();
        } catch (Exception e) {

            if (transaction.isActive()) transaction.rollback();
            fail("Error while updating trainer: " + e.getMessage());
        }

        assertEquals("Anna", result.getUser().getFirstName());

        try (Session verifySession = sessionFactory.openSession()) {
            Trainer saved = verifySession.find(Trainer.class, id);

            assertEquals("Anna", saved.getUser().getFirstName());
        }
    }

    @Test
    void update_whenTrainerDoesNotExist_throwsEntityNotFoundException() {
        User user = createUser();
        TrainingType specialization = saveTrainingType(createTrainingType());
        Trainer trainer = createTrainer(user, specialization);
        trainer = trainer.toBuilder().id(999L).build();

        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();

        try {
            Trainer finalTrainer = trainer;
            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> dao.update(finalTrainer));

            assertEquals("Trainer with id 999 not found", ex.getMessage());
            transaction.rollback();
        } catch (Exception e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }

            fail("Error while updating non-existent trainer: " + e.getMessage());
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

    private User createUser(String firstName) {
        return User.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .isActive(true)
                .firstName(firstName)
                .lastName(LAST_NAME)
                .build();
    }

    private Trainer createTrainer(User user, TrainingType specialization) {
        return Trainer.builder()
                .user(user)
                .specialization(specialization)
                .build();
    }

    private TrainingType createTrainingType() {
        return TrainingType.builder()
                .name(SPECIALIZATION)
                .build();
    }

    private TrainingType saveTrainingType(TrainingType type) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            TrainingType existing = session.createQuery("FROM TrainingType t WHERE t.name = :name", TrainingType.class)
                    .setParameter("name", type.getName())
                    .uniqueResult();

            if (existing != null) {
                session.getTransaction().commit();
                return existing;
            }

            session.persist(type);
            session.getTransaction().commit();
            return type;
        } catch (Exception e) {
            fail("Failed to save specialization: " + e.getMessage());
            return null;
        }
    }

    private Trainer saveTrainer() {
        User user = createUser();
        TrainingType specialization = saveTrainingType(createTrainingType());
        Trainer trainer = createTrainer(user, specialization);

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(user);
            session.persist(trainer);
            session.getTransaction().commit();
        } catch (Exception e) {
            fail("Failed to save trainer: " + e.getMessage());
        }

        return trainer;
    }
}