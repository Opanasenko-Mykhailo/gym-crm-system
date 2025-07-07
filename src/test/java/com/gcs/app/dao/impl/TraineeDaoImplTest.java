package com.gcs.app.dao.impl;

import com.gcs.app.dao.TestRepository;
import com.gcs.app.exception.EntityNotFoundException;
import com.gcs.app.model.Trainee;
import com.gcs.app.model.User;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class TraineeDaoImplTest extends TestRepository<TraineeDaoImpl> {

    private static final Long EXISTING_TRAINEE_ID = 1L;
    private static final Long NON_EXISTENT_TRAINEE_ID = 999L;

    @Override
    protected TraineeDaoImpl initDao() {
        return new TraineeDaoImpl(sessionFactory);
    }

    @Override
    protected String getXmlDataPath() {
        return "/dbunit/trainee-data.xml";
    }

    @Test
    void get_whenTraineeExists_returnsTrainee() {
        Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();

        Optional<Trainee> result = dao.get(EXISTING_TRAINEE_ID);

        transaction.commit();

        assertTrue(result.isPresent());
        assertEquals("john.doe", result.get().getUser().getUsername());
        assertEquals("John", result.get().getUser().getFirstName());
    }

    @Test
    void get_whenTraineeDoesNotExist_returnsEmptyOptional() {
        Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();

        Optional<Trainee> result = dao.get(NON_EXISTENT_TRAINEE_ID);

        transaction.commit();

        assertFalse(result.isPresent());
    }

    @Test
    void update_whenTraineeExists_mergesAndReturnsTrainee() {
        Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();

        Trainee existing = dao.get(EXISTING_TRAINEE_ID).orElseThrow();
        User updatedUser = existing.getUser().toBuilder()
                .firstName("UpdatedName")
                .build();

        Trainee updated = existing.toBuilder()
                .user(updatedUser)
                .address("Updated Address")
                .build();

        Trainee result = dao.update(updated);

        transaction.commit();

        assertEquals("UpdatedName", result.getUser().getFirstName());
        assertEquals("Updated Address", result.getAddress());
    }

    @Test
    void update_whenTraineeDoesNotExist_throwsException() {
        Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();

        User user = User.builder()
                .id(2L)
                .firstName("Ghost")
                .lastName("User")
                .username("ghost.user")
                .password("password")
                .isActive(true)
                .build();

        Trainee trainee = Trainee.builder()
                .id(NON_EXISTENT_TRAINEE_ID)
                .user(user)
                .address("Phantom Address")
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .build();

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> dao.update(trainee));

        transaction.commit();

        assertEquals("Trainee with id 999 not found", exception.getMessage());
    }

    @Test
    void delete_whenTraineeExists_removesTrainee() {
        Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();
        dao.delete(EXISTING_TRAINEE_ID);
        transaction.commit();

        Transaction verificationTransaction = sessionFactory.getCurrentSession().beginTransaction();

        assertFalse(dao.get(EXISTING_TRAINEE_ID).isPresent());

        verificationTransaction.commit();
    }

    @Test
    void delete_whenTraineeDoesNotExist_throwsException() {
        Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> dao.delete(NON_EXISTENT_TRAINEE_ID));

        transaction.commit();

        assertEquals("Trainee with id 999 not found", exception.getMessage());
    }
}
