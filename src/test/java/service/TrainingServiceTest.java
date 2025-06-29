package service;

import com.gcs.app.dao.TrainingDao;
import com.gcs.app.exception.ServiceException;
import com.gcs.app.facade.dto.TrainingCreateRequestDto;
import com.gcs.app.mapper.TrainingMapper;
import com.gcs.app.model.Training;
import com.gcs.app.model.TrainingType;
import com.gcs.app.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TrainingServiceTest {

    @Mock
    private TrainingDao trainingDao;

    @Mock
    private TrainingMapper trainingMapper;

    @InjectMocks
    private TrainingServiceImpl sut;

    private Training training;
    private TrainingCreateRequestDto createRequestDto;

    @BeforeEach
    void setUp() {
        TrainingType trainingType = new TrainingType("Yoga");
        MockitoAnnotations.openMocks(this);
        training = Training.builder()
                .id(1L)
                .traineeId(2L)
                .trainerId(3L)
                .name("Yoga Session")
                .type(trainingType)
                .date(LocalDate.of(2025, 6, 30))
                .duration(Duration.ofHours(1))
                .build();
        createRequestDto = new TrainingCreateRequestDto();
        createRequestDto.setTraineeId(2L);
        createRequestDto.setTrainerId(3L);
        createRequestDto.setName("Yoga Session");
        createRequestDto.setType(trainingType);
        createRequestDto.setDate(LocalDate.of(2025, 6, 30));
        createRequestDto.setDuration(Duration.ofHours(1));
    }

    @Test
    void createTraining_mapsDtoAndCreatesTraining_returnsTraining() {
        when(trainingMapper.toEntity(createRequestDto)).thenReturn(training);
        when(trainingDao.create(training)).thenReturn(training);

        Training result = sut.createTraining(createRequestDto);

        assertEquals(training, result);

        verify(trainingMapper, times(1)).toEntity(createRequestDto);
        verify(trainingDao, times(1)).create(training);
    }

    @Test
    void getTraining_whenTrainingExists_returnsTraining() {
        when(trainingDao.get(1L)).thenReturn(Optional.of(training));

        Training result = sut.getTraining(1L);

        assertEquals(training, result);
        verify(trainingDao, times(1)).get(1L);
    }

    @Test
    void getTraining_whenTrainingDoesNotExist_throwsServiceException() {
        when(trainingDao.get(1L)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> sut.getTraining(1L));
        assertEquals("Training with id 1 not found", exception.getMessage());

        verify(trainingDao, times(1)).get(1L);
    }
}