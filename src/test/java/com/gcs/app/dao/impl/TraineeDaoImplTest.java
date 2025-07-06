package com.gcs.app.dao.impl;

import com.gcs.app.config.TestConfig;
import com.gcs.app.exception.EntityNotFoundException;
import com.gcs.app.model.Trainee;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
class TraineeDaoImplTest {

    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String USERNAME = "john.doe";
    private static final String PASSWORD = "password";
    private static final LocalDate DATE_OF_BIRTH = LocalDate.of(1990, 1, 1);
    private static final String ADDRESS = "123 Main St";

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private TraineeDaoImpl dao;

    @Test
    void create_persistsTrainee_returnsTrainee() {
        User user = createUser(FIRST_NAME);
        Trainee trainee = createTrainee(user);

        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();

        try {
            dao.create(trainee);
            transaction.commit();
        } catch (Exception e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }
            fail("Error while creating trainee: " + e.getMessage());
        }

        try (Session verifySession = sessionFactory.openSession()) {
            Trainee savedTrainee = verifySession.find(Trainee.class, trainee.getId());

            assertNotNull(savedTrainee);
            assertEquals(USERNAME, savedTrainee.getUser().getUsername());
        }
    }

    @Test
    void get_whenTraineeExists_returnsOptionalOfTrainee() {
        Trainee trainee = saveNewTrainee();
        Long id = trainee.getId();

        Optional<Trainee> result = Optional.empty();
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();

        try {
            result = dao.get(id);
            transaction.commit();
        } catch (Exception e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }
            fail("Error while retrieving trainee: " + e.getMessage());
        }

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        assertEquals(USERNAME, result.get().getUser().getUsername());
    }

    @Test
    void get_whenTraineeDoesNotExist_returnsEmptyOptional() {
        Optional<Trainee> result = Optional.empty();
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();

        try {
            result = dao.get(999L);
            transaction.commit();
        } catch (Exception e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }
            fail("Error while attempting to retrieve non-existent trainee: " + e.getMessage());
        }

        assertFalse(result.isPresent());
    }

    @Test
    void update_whenTraineeExists_mergesAndReturnsTrainee() {
        Trainee original = saveNewTrainee();
        Long id = original.getId();

        User updatedUser = createUser("Jane").toBuilder()
                .username(USERNAME)
                .password(PASSWORD)
                .isActive(true)
                .lastName(LAST_NAME)
                .build();

        Trainee updatedTrainee = createTrainee(updatedUser).toBuilder()
                .id(id)
                .build();

        Trainee result = null;
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();

        try {
            result = dao.update(updatedTrainee);
            transaction.commit();
        } catch (Exception e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }
            fail("Error while updating trainee: " + e.getMessage());
        }

        assertNotNull(result);
        assertEquals("Jane", result.getUser().getFirstName());

        try (Session verifySession = sessionFactory.openSession()) {
            Trainee savedTrainee = verifySession.find(Trainee.class, id);
            assertEquals("Jane", savedTrainee.getUser().getFirstName());
        }
    }

    @Test
    void update_whenTraineeDoesNotExist_throwsEntityNotFoundException() {
        User user = createUser(FIRST_NAME);
        Trainee trainee = createTrainee(user).toBuilder().id(999L).build();

        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();

        try {
            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> dao.update(trainee));

            assertEquals("Trainee with id 999 not found", ex.getMessage());
            transaction.rollback();
        } catch (Exception e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }
            fail("Error while updating non-existent trainee: " + e.getMessage());
        }
    }

    @Test
    void delete_whenTraineeExists_removesTrainee() {
        Trainee trainee = saveNewTrainee();
        Long id = trainee.getId();

        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();
        try {
            dao.delete(id);
            transaction.commit();
        } catch (Exception e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }
            fail("Error while deleting trainee: " + e.getMessage());
        }

        try (Session verifySession = sessionFactory.openSession()) {
            Trainee deletedTrainee = verifySession.find(Trainee.class, id);

            assertNull(deletedTrainee);
        }
    }

    @Test
    void delete_whenTraineeDoesNotExist_throwsEntityNotFoundException() {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();
        try {
            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> dao.delete(999L));

            assertEquals("Trainee with id 999 not found", ex.getMessage());
            transaction.rollback();
        } catch (Exception e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }
            fail("Error while attempting to delete non-existent trainee: " + e.getMessage());
        }
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

    private Trainee createTrainee(User user) {
        return Trainee.builder()
                .user(user)
                .dateOfBirth(DATE_OF_BIRTH)
                .address(ADDRESS)
                .build();
    }

    private Trainee saveNewTrainee() {
        User user = createUser(FIRST_NAME);
        Trainee trainee = createTrainee(user);

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(user);
            session.persist(trainee);
            session.getTransaction().commit();
        } catch (Exception e) {
            fail("Error while saving new trainee: " + e.getMessage());
        }

        return trainee;
    }
}