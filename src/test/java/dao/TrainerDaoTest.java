package dao;

import com.gcs.app.dao.impl.TrainerDaoImpl;
import com.gcs.app.exception.EntityNotFoundException;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.TrainingType;
import com.gcs.app.storage.InMemoryStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;
import java.util.Optional;

import static com.gcs.app.model.enums.EntityType.TRAINER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TrainerDaoTest {

    @Mock
    private InMemoryStorage storage;

    @Mock
    private Map<Long, Object> trainerStorage;

    @InjectMocks
    private TrainerDaoImpl sut;

    private Trainer trainer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        trainer = Trainer.builder()
                .userId(1L)
                .firstName("Jane")
                .lastName("Smith")
                .username("jane.smith")
                .password("password")
                .isActive(true)
                .specialization(new TrainingType("Yoga"))
                .build();
    }

    @Test
    void create_assignsUserIdAndStoresTrainer_returnsTrainer() {
        when(storage.nextId()).thenReturn(1L);
        when(storage.getNamespace(TRAINER)).thenReturn(trainerStorage);

        Trainer result = sut.create(trainer);

        assertEquals(1L, result.getUserId());
        assertEquals(trainer, result);

        verify(storage, times(1)).nextId();
        verify(storage, times(1)).put(TRAINER, 1L, trainer);
    }

    @Test
    void get_whenTrainerExists_returnsTrainer() {
        when(storage.getById(TRAINER, 1L)).thenReturn(Optional.of(trainer));

        Optional<Trainer> result = sut.get(1L);

        assertTrue(result.isPresent());
        assertEquals(trainer, result.get());

        verify(storage, times(1)).getById(TRAINER, 1L);
    }

    @Test
    void get_whenTrainerDoesNotExist_returnsEmptyOptional() {
        when(storage.getById(TRAINER, 1L)).thenReturn(Optional.empty());

        Optional<Trainer> result = sut.get(1L);

        assertFalse(result.isPresent());

        verify(storage, times(1)).getById(TRAINER, 1L);
    }

    @Test
    void update_whenTrainerExists_updatesAndReturnsTrainer() {
        when(storage.getById(TRAINER, 1L)).thenReturn(Optional.of(trainer));
        when(storage.getNamespace(TRAINER)).thenReturn(trainerStorage);

        Trainer result = sut.update(trainer);

        assertEquals(trainer, result);

        verify(storage, times(1)).getById(TRAINER, 1L);
        verify(storage, times(1)).put(TRAINER, 1L, trainer);
    }

    @Test
    void update_whenTrainerDoesNotExist_throwsEntityNotFoundException() {
        when(storage.getById(TRAINER, 1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> sut.update(trainer));
        assertEquals("Trainer with userId: 1 not found", exception.getMessage());

        verify(storage, times(1)).getById(TRAINER, 1L);
        verify(storage, times(0)).put(TRAINER, 1L, trainer);
    }
}