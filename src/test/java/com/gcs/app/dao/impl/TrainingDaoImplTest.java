package com.gcs.app.dao.impl;

import com.gcs.app.config.TestConfig;
import com.gcs.app.model.Trainee;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.Training;
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

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
class TrainingDaoImplTest {

    private static final String NAME = "Yoga Session";
    private static final String TYPE = "Yoga";
    private static final LocalDate DATE = LocalDate.of(2025, 6, 30);
    private static final Long DURATION = 60L;

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private TrainingDaoImpl dao;

    @Test
    void create_persistsTraining_returnsTraining() {
        Trainee trainee = saveTrainee();
        Trainer trainer = saveTrainer();
        TrainingType type = saveTrainingType();

        Training training = createTraining(trainee, trainer, type);

        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();

        try {
            dao.create(training);
            transaction.commit();
        } catch (Exception e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }

            fail("Error while saving training: " + e.getMessage());
        }

        try (Session verifySession = sessionFactory.openSession()) {
            Training saved = verifySession.find(Training.class, training.getId());

            assertNotNull(saved);
            assertEquals(NAME, saved.getName());
            assertEquals(DATE, saved.getDate());
            assertEquals(DURATION, saved.getDuration());
            assertEquals(TYPE, saved.getType().getName());
            assertEquals(trainee.getId(), saved.getTrainee().getId());
            assertEquals(trainer.getId(), saved.getTrainer().getId());
        }
    }

    @Test
    void get_whenTrainingExists_returnsTraining() {
        Training training = saveTraining();

        Training result = null;
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();

        try {
            Optional<Training> optionalTraining = dao.get(training.getId());
            result = optionalTraining.orElse(null);
            transaction.commit();
        } catch (Exception e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }

            fail("Failed to retrieve training: " + e.getMessage());
        }

        assertNotNull(result);
        assertEquals(NAME, result.getName());
        assertEquals(TYPE, result.getType().getName());
        assertEquals(training.getTrainee().getId(), result.getTrainee().getId());
        assertEquals(training.getTrainer().getId(), result.getTrainer().getId());
    }

    @Test
    void get_whenTrainingDoesNotExist_returnsEmptyOptional() {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();
        Optional<Training> result = dao.get(999L);
        transaction.commit();

        assertFalse(result.isPresent());
    }

    private Training createTraining(Trainee trainee, Trainer trainer, TrainingType type) {
        return Training.builder()
                .trainee(trainee)
                .trainer(trainer)
                .type(type)
                .name(NAME)
                .date(DATE)
                .duration(DURATION)
                .build();
    }

    private Training saveTraining() {
        Trainee trainee = saveTrainee();
        Trainer trainer = saveTrainer();
        TrainingType type = saveTrainingType();

        Training training = createTraining(trainee, trainer, type);

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(training);
            transaction.commit();
        } catch (Exception e) {
            fail("Failed to save training: " + e.getMessage());
        }

        return training;
    }

    private Trainee saveTrainee() {
        User user = createUser("trainee.user", "John", "Doe");
        Trainee trainee = Trainee.builder()
                .user(user)
                .build();

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            session.persist(user);
            session.persist(trainee);

            transaction.commit();
        } catch (Exception e) {
            fail("Failed to save trainee: " + e.getMessage());
        }

        return trainee;
    }

    private Trainer saveTrainer() {
        User user = createUser("trainer.user", "Jane", "Smith");
        Trainer trainer = Trainer.builder()
                .user(user)
                .specialization(saveTrainingType())
                .build();

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            session.persist(user);
            session.persist(trainer);

            transaction.commit();
        } catch (Exception e) {
            fail("Failed to save trainer: " + e.getMessage());
        }

        return trainer;
    }

    private TrainingType saveTrainingType() {
        TrainingType type = TrainingType.builder()
                .name(TYPE)
                .build();

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            TrainingType existing = session.createQuery("FROM TrainingType t WHERE t.name = :name", TrainingType.class)
                    .setParameter("name", TYPE)
                    .uniqueResult();

            if (existing != null) {
                transaction.commit();
                return existing;
            }

            session.persist(type);
            transaction.commit();
            return type;
        } catch (Exception e) {
            fail("Failed to save training type: " + e.getMessage());
            return null;
        }
    }

    private User createUser(String username, String firstName, String lastName) {
        return User.builder()
                .username(username)
                .password("password")
                .isActive(true)
                .firstName(firstName)
                .lastName(lastName)
                .build();
    }
}