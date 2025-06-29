package dao;

import com.gcs.app.dao.impl.TraineeDaoImpl;
import com.gcs.app.exception.EntityNotFoundException;
import com.gcs.app.model.Trainee;
import com.gcs.app.storage.InMemoryStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static com.gcs.app.model.enums.EntityType.TRAINEE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TraineeDaoTest {

    @Mock
    private InMemoryStorage storage;

    @Mock
    private Map<Long, Object> traineeStorage;

    @InjectMocks
    private TraineeDaoImpl sut;

    private Trainee trainee;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        trainee = Trainee.builder()
                .userId(1L)
                .firstName("John")
                .lastName("Doe")
                .username("john.doe")
                .password("password")
                .isActive(true)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .build();
    }

    @Test
    void create_assignsUserIdAndStoresTrainee_returnsTrainee() {
        when(storage.nextId()).thenReturn(1L);
        when(storage.getNamespace(TRAINEE)).thenReturn(traineeStorage);

        Trainee result = sut.create(trainee);

        assertEquals(1L, result.getUserId());
        assertEquals(trainee, result);

        verify(storage, times(1)).nextId();
        verify(storage, times(1)).put(TRAINEE, 1L, trainee);
    }

    @Test
    void get_whenTraineeExists_returnsTrainee() {
        when(storage.getById(TRAINEE, 1L)).thenReturn(Optional.of(trainee));

        Optional<Trainee> result = sut.get(1L);

        assertTrue(result.isPresent());
        assertEquals(trainee, result.get());

        verify(storage, times(1)).getById(TRAINEE, 1L);
    }

    @Test
    void get_whenTraineeDoesNotExist_returnsEmptyOptional() {
        when(storage.getById(TRAINEE, 1L)).thenReturn(Optional.empty());

        Optional<Trainee> result = sut.get(1L);

        assertFalse(result.isPresent());

        verify(storage, times(1)).getById(TRAINEE, 1L);
    }

    @Test
    void update_whenTraineeExists_updatesAndReturnsTrainee() {
        when(storage.getById(TRAINEE, 1L)).thenReturn(Optional.of(trainee));
        when(storage.getNamespace(TRAINEE)).thenReturn(traineeStorage);

        Trainee result = sut.update(trainee);

        assertEquals(trainee, result);

        verify(storage, times(1)).getById(TRAINEE, 1L);
        verify(storage, times(1)).put(TRAINEE, 1L, trainee);
    }

    @Test
    void update_whenTraineeDoesNotExist_throwsEntityNotFoundException() {
        when(storage.getById(TRAINEE, 1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> sut.update(trainee));
        assertEquals("Trainee with userId: 1 not found", exception.getMessage());

        verify(storage, times(1)).getById(TRAINEE, 1L);
        verify(storage, times(0)).put(TRAINEE, 1L, trainee);
    }

    @Test
    void delete_whenTraineeExists_removesTrainee() {
        when(storage.getById(TRAINEE, 1L)).thenReturn(Optional.of(trainee));
        when(storage.getNamespace(TRAINEE)).thenReturn(traineeStorage);

        sut.delete(1L);

        verify(storage, times(1)).getById(TRAINEE, 1L);
        verify(traineeStorage, times(1)).remove(1L);
    }

    @Test
    void delete_whenTraineeDoesNotExist_throwsEntityNotFoundException() {
        when(storage.getById(TRAINEE, 1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> sut.delete(1L));
        assertEquals("Trainee with userId: 1 not found", exception.getMessage());

        verify(storage, times(1)).getById(TRAINEE, 1L);
        verify(storage, times(0)).getNamespace(TRAINEE);
    }
}