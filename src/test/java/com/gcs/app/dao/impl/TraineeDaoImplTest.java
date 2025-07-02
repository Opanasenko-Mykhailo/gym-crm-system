package com.gcs.app.dao.impl;

import com.gcs.app.exception.EntityNotFoundException;
import com.gcs.app.model.Trainee;
import com.gcs.app.model.User;
import com.gcs.app.storage.InMemoryStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static com.gcs.app.model.enums.EntityType.TRAINEE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
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
    private InMemoryStorage storage;

    @Mock
    private Map<Long, Object> traineeNamespace;

    @InjectMocks
    private TraineeDaoImpl dao;

    private Trainee expected;

    @BeforeEach
    void setUp() {
        expected = createTrainee();
    }

    @Test
    void create_assignsUserIdAndStoresTrainee_returnsTrainee() {
        when(storage.nextId()).thenReturn(USER_ID);

        Trainee actual = dao.create(expected);

        assertEquals(USER_ID, actual.getId());
        assertEquals(FIRST_NAME, actual.getUser().getFirstName());
        assertEquals(LAST_NAME, actual.getUser().getLastName());
        assertEquals(USERNAME, actual.getUser().getUsername());
        assertEquals(PASSWORD, actual.getUser().getPassword());
        assertTrue(actual.getUser().getIsActive());
        assertEquals(DATE_OF_BIRTH, actual.getDateOfBirth());
        assertEquals(ADDRESS, actual.getAddress());

        verify(storage).nextId();
        verify(storage).put(TRAINEE, USER_ID, expected);
    }

    @Test
    void get_whenTraineeExists_returnsTrainee() {
        when(storage.getById(TRAINEE, USER_ID)).thenReturn(Optional.of(expected));

        Optional<Trainee> actual = dao.get(USER_ID);

        assertTrue(actual.isPresent());
        assertEquals(FIRST_NAME, actual.get().getUser().getFirstName());
        assertEquals(LAST_NAME, actual.get().getUser().getLastName());
        assertEquals(USERNAME, actual.get().getUser().getUsername());

        verify(storage).getById(TRAINEE, USER_ID);
    }

    @Test
    void get_whenTraineeDoesNotExist_returnsEmptyOptional() {
        when(storage.getById(TRAINEE, USER_ID)).thenReturn(Optional.empty());

        Optional<Trainee> actual = dao.get(USER_ID);

        assertFalse(actual.isPresent());
        verify(storage).getById(TRAINEE, USER_ID);
    }

    @Test
    void update_whenTraineeExists_updatesAndReturnsTrainee() {
        when(storage.getById(TRAINEE, USER_ID)).thenReturn(Optional.of(expected));

        Trainee actual = dao.update(expected);

        assertEquals(FIRST_NAME, actual.getUser().getFirstName());
        assertEquals(LAST_NAME, actual.getUser().getLastName());
        assertEquals(USERNAME, actual.getUser().getUsername());

        verify(storage).getById(TRAINEE, USER_ID);
        verify(storage).put(TRAINEE, USER_ID, expected);
    }

    @Test
    void update_whenTraineeDoesNotExist_throwsEntityNotFoundException() {
        when(storage.getById(TRAINEE, USER_ID)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> dao.update(expected));

        assertEquals("Trainee with userId: 1 not found", ex.getMessage());
        verify(storage).getById(TRAINEE, USER_ID);
        verify(storage, never()).put(TRAINEE, USER_ID, expected);
    }

    @Test
    void delete_whenTraineeExists_removesTrainee() {
        when(storage.getById(TRAINEE, USER_ID)).thenReturn(Optional.of(expected));
        when(storage.getNamespace(TRAINEE)).thenReturn(traineeNamespace);

        dao.delete(USER_ID);

        verify(storage).getById(TRAINEE, USER_ID);
        verify(traineeNamespace).remove(USER_ID);
    }

    @Test
    void delete_whenTraineeDoesNotExist_throwsEntityNotFoundException() {
        when(storage.getById(TRAINEE, USER_ID)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> dao.delete(USER_ID));

        assertEquals("Trainee with userId: 1 not found", ex.getMessage());
        verify(storage).getById(TRAINEE, USER_ID);
        verify(storage, never()).getNamespace(TRAINEE);
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