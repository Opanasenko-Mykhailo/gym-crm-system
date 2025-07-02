package com.gcs.app.dao.impl;

import com.gcs.app.exception.EntityNotFoundException;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.TrainingType;
import com.gcs.app.model.User;
import com.gcs.app.storage.InMemoryStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.gcs.app.model.enums.EntityType.TRAINER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
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
    private InMemoryStorage storage;

    @InjectMocks
    private TrainerDaoImpl dao;

    private Trainer expected;

    @BeforeEach
    void setUp() {
        expected = buildTrainer();
    }

    @Test
    void create_assignsUserIdAndStoresTrainer_returnsTrainer() {
        when(storage.nextId()).thenReturn(USER_ID);

        Trainer actual = dao.create(expected);

        assertEquals(USER_ID, actual.getId());
        assertEquals(FIRST_NAME, actual.getUser().getFirstName());
        assertEquals(LAST_NAME, actual.getUser().getLastName());
        assertEquals(USERNAME, actual.getUser().getUsername());
        assertEquals(PASSWORD, actual.getUser().getPassword());
        assertTrue(actual.getUser().getIsActive());
        assertEquals(SPECIALIZATION, actual.getSpecialization().getName());

        verify(storage).nextId();
        verify(storage).put(TRAINER, USER_ID, expected);
    }

    @Test
    void get_whenTrainerExists_returnsTrainer() {
        when(storage.getById(TRAINER, USER_ID)).thenReturn(Optional.of(expected));

        Optional<Trainer> actual = dao.get(USER_ID);

        assertTrue(actual.isPresent());
        assertEquals(FIRST_NAME, actual.get().getUser().getFirstName());
        assertEquals(LAST_NAME, actual.get().getUser().getLastName());
        assertEquals(USERNAME, actual.get().getUser().getUsername());
        assertEquals(SPECIALIZATION, actual.get().getSpecialization().getName());

        verify(storage).getById(TRAINER, USER_ID);
    }

    @Test
    void get_whenTrainerDoesNotExist_returnsEmptyOptional() {
        when(storage.getById(TRAINER, USER_ID)).thenReturn(Optional.empty());

        Optional<Trainer> actual = dao.get(USER_ID);

        assertFalse(actual.isPresent());
        verify(storage).getById(TRAINER, USER_ID);
    }

    @Test
    void update_whenTrainerExists_updatesAndReturnsTrainer() {
        when(storage.getById(TRAINER, USER_ID)).thenReturn(Optional.of(expected));

        Trainer actual = dao.update(expected);

        assertEquals(FIRST_NAME, actual.getUser().getFirstName());
        assertEquals(LAST_NAME, actual.getUser().getLastName());
        assertEquals(USERNAME, actual.getUser().getUsername());
        assertEquals(SPECIALIZATION, actual.getSpecialization().getName());

        verify(storage).getById(TRAINER, USER_ID);
        verify(storage).put(TRAINER, USER_ID, expected);
    }

    @Test
    void update_whenTrainerDoesNotExist_throwsEntityNotFoundException() {
        when(storage.getById(TRAINER, USER_ID)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> dao.update(expected));

        assertEquals("Trainer with userId: 1 not found", ex.getMessage());
        verify(storage).getById(TRAINER, USER_ID);
        verify(storage, never()).put(TRAINER, USER_ID, expected);
    }

    private Trainer buildTrainer() {
        return Trainer.builder()
                .id(USER_ID)
                .user(User.builder()
                        .username(USERNAME)
                        .password(PASSWORD)
                        .isActive(true)
                        .firstName(FIRST_NAME)
                        .lastName(LAST_NAME)
                        .build())
                .specialization(TrainingType.builder()
                        .name(SPECIALIZATION)
                        .build())
                .build();
    }
}