package com.gcs.app.dao.impl;

import com.gcs.app.exception.EntityNotFoundException;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.TrainingType;
import com.gcs.app.model.User;
import org.hibernate.IdentifierLoadAccess;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerDaoImplTest {

    private static final Long USER_ID = 1L;
    private static final String FIRST_NAME = "Jane";
    private static final String LAST_NAME = "Smith";
    private static final String USERNAME = "jane.smith";
    private static final String PASSWORD = "password";
    private static final String SPECIALIZATION = "Yoga";

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private IdentifierLoadAccess<Trainer> identifierLoadAccess;

    @InjectMocks
    private TrainerDaoImpl dao;

    @Test
    void create_persistsTrainer_returnsTrainer() {
        Trainer trainer = createTrainer();

        when(sessionFactory.getCurrentSession()).thenReturn(session);

        Trainer actual = dao.create(trainer);

        verify(session).persist(trainer);
        assertEquals(USER_ID, actual.getId());
        assertEquals(FIRST_NAME, actual.getUser().getFirstName());
        assertEquals(LAST_NAME, actual.getUser().getLastName());
        assertEquals(USERNAME, actual.getUser().getUsername());
        assertEquals(PASSWORD, actual.getUser().getPassword());
        assertTrue(actual.getUser().getIsActive());
        assertEquals(SPECIALIZATION, actual.getSpecialization().getName());
    }

    @Test
    void get_whenTrainerExists_returnsTrainer() {
        Trainer trainer = createTrainer();

        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.byId(Trainer.class)).thenReturn(identifierLoadAccess);
        when(identifierLoadAccess.load(USER_ID)).thenReturn(trainer);

        var result = dao.get(USER_ID);

        assertTrue(result.isPresent());
        assertEquals(trainer, result.get());
    }

    @Test
    void get_whenTrainerDoesNotExist_returnsEmptyOptional() {
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.byId(Trainer.class)).thenReturn(identifierLoadAccess);
        when(identifierLoadAccess.load(USER_ID)).thenReturn(null);

        var result = dao.get(USER_ID);

        assertFalse(result.isPresent());
    }

    @Test
    void update_whenTrainerExists_mergesAndReturnsTrainer() {
        Trainer trainer = createTrainer();

        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.byId(Trainer.class)).thenReturn(identifierLoadAccess);
        when(identifierLoadAccess.load(USER_ID)).thenReturn(trainer);
        when(session.merge(trainer)).thenReturn(trainer);

        Trainer updated = dao.update(trainer);

        assertEquals(trainer, updated);
        verify(session).merge(trainer);
    }

    @Test
    void update_whenTrainerDoesNotExist_throwsEntityNotFoundException() {
        Trainer trainer = createTrainer();

        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.byId(Trainer.class)).thenReturn(identifierLoadAccess);
        when(identifierLoadAccess.load(USER_ID)).thenReturn(null);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> dao.update(trainer));

        assertEquals("Trainer with id 1 not found", ex.getMessage());
    }

    private Trainer createTrainer() {
        return Trainer.builder()
                .id(USER_ID)
                .user(createUser())
                .specialization(createTrainingType())
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

    private TrainingType createTrainingType() {
        return TrainingType.builder()
                .name(SPECIALIZATION)
                .build();
    }
}
