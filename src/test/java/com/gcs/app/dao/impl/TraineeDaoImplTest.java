package com.gcs.app.dao.impl;

import com.gcs.app.exception.EntityNotFoundException;
import com.gcs.app.model.Trainee;
import com.gcs.app.model.User;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeDaoImplTest {

    private static final Long USER_ID = 1L;
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String USERNAME = "john.doe";
    private static final String PASSWORD = "password";
    private static final LocalDate DATE_OF_BIRTH = LocalDate.of(1990, 1, 1);
    private static final String ADDRESS = "123 Main St";

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private IdentifierLoadAccess<Trainee> identifierLoadAccess;

    @InjectMocks
    private TraineeDaoImpl dao;

    @Test
    void create_persistsTrainee_returnsTrainee() {
        Trainee trainee = createTrainee();

        when(sessionFactory.getCurrentSession()).thenReturn(session);

        dao.create(trainee);

        verify(session).persist(trainee);
        assertEquals(USER_ID, trainee.getId());
    }

    @Test
    void get_whenTraineeExists_returnsOptionalOfTrainee() {
        Trainee trainee = createTrainee();

        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.byId(Trainee.class)).thenReturn(identifierLoadAccess);
        when(identifierLoadAccess.load(USER_ID)).thenReturn(trainee);

        Optional<Trainee> result = dao.get(USER_ID);

        assertTrue(result.isPresent());
        assertEquals(trainee, result.get());
    }

    @Test
    void get_whenTraineeDoesNotExist_returnsEmptyOptional() {
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.byId(Trainee.class)).thenReturn(identifierLoadAccess);
        when(identifierLoadAccess.load(USER_ID)).thenReturn(null);

        Optional<Trainee> result = dao.get(USER_ID);

        assertFalse(result.isPresent());
    }

    @Test
    void update_whenTraineeExists_mergesAndReturnsTrainee() {
        Trainee trainee = createTrainee();

        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.byId(Trainee.class)).thenReturn(identifierLoadAccess);
        when(identifierLoadAccess.load(USER_ID)).thenReturn(trainee);
        when(session.merge(trainee)).thenReturn(trainee);

        Trainee updated = dao.update(trainee);

        assertEquals(trainee, updated);
        verify(session).merge(trainee);
    }

    @Test
    void update_whenTraineeDoesNotExist_throwsEntityNotFoundException() {
        Trainee trainee = createTrainee();

        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.byId(Trainee.class)).thenReturn(identifierLoadAccess);
        when(identifierLoadAccess.load(USER_ID)).thenReturn(null);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> dao.update(trainee));

        assertEquals("Trainee with id 1 not found", ex.getMessage());
    }

    @Test
    void delete_whenTraineeExists_removesTrainee() {
        Trainee trainee = createTrainee();

        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.byId(Trainee.class)).thenReturn(identifierLoadAccess);
        when(identifierLoadAccess.load(USER_ID)).thenReturn(trainee);

        dao.delete(USER_ID);

        verify(session).remove(trainee);
    }

    @Test
    void delete_whenTraineeDoesNotExist_throwsEntityNotFoundException() {
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.byId(Trainee.class)).thenReturn(identifierLoadAccess);
        when(identifierLoadAccess.load(USER_ID)).thenReturn(null);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> dao.delete(USER_ID));

        assertEquals("Trainee with id 1 not found", ex.getMessage());
    }

    private Trainee createTrainee() {
        return Trainee.builder()
                .id(USER_ID)
                .user(createUser())
                .dateOfBirth(DATE_OF_BIRTH)
                .address(ADDRESS)
                .build();
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
}
