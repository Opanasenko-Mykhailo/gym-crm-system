package dao;

import com.gcs.app.dao.impl.TrainingDaoImpl;
import com.gcs.app.model.Training;
import com.gcs.app.model.TrainingType;
import com.gcs.app.storage.InMemoryStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static com.gcs.app.model.enums.EntityType.TRAINING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TrainingDaoTest {

    @Mock
    private InMemoryStorage storage;

    @Mock
    private Map<Long, Object> trainingStorage;

    @InjectMocks
    private TrainingDaoImpl sut;

    private Training training;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        training = Training.builder()
                .id(1L)
                .traineeId(2L)
                .trainerId(3L)
                .name("Yoga Session")
                .type(new TrainingType("Yoga"))
                .date(LocalDate.of(2025, 6, 30))
                .duration(Duration.ofHours(1))
                .build();
    }

    @Test
    void create_assignsIdAndStoresTraining_returnsTraining() {
        when(storage.nextId()).thenReturn(1L);
        when(storage.getNamespace(TRAINING)).thenReturn(trainingStorage);

        Training result = sut.create(training);

        assertEquals(1L, result.getId());
        assertEquals(training, result);

        verify(storage, times(1)).nextId();
        verify(storage, times(1)).put(TRAINING, 1L, training);
    }

    @Test
    void get_whenTrainingExists_returnsTraining() {
        when(storage.getById(TRAINING, 1L)).thenReturn(Optional.of(training));

        Optional<Training> result = sut.get(1L);

        assertTrue(result.isPresent());
        assertEquals(training, result.get());

        verify(storage, times(1)).getById(TRAINING, 1L);
    }

    @Test
    void get_whenTrainingDoesNotExist_returnsEmptyOptional() {
        when(storage.getById(TRAINING, 1L)).thenReturn(Optional.empty());

        Optional<Training> result = sut.get(1L);

        assertFalse(result.isPresent());

        verify(storage, times(1)).getById(TRAINING, 1L);
    }
}