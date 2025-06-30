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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    private static final Long ID = 1L;
    private static final Long TRAINEE_ID = 2L;
    private static final Long TRAINER_ID = 3L;
    private static final String NAME = "Yoga Session";
    private static final String TYPE = "Yoga";
    private static final LocalDate DATE = LocalDate.of(2025, 6, 30);
    private static final Duration DURATION = Duration.ofHours(1);

    @Mock
    private TrainingDao trainingDao;

    @Mock
    private TrainingMapper trainingMapper;

    @InjectMocks
    private TrainingServiceImpl service;

    private Training expected;
    private TrainingCreateRequestDto createRequestDto;

    @BeforeEach
    void setUp() {
        expected = buildTraining();
        createRequestDto = buildCreateRequestDto();
    }

    private Training buildTraining() {
        return Training.builder()
                .id(ID)
                .traineeId(TRAINEE_ID)
                .trainerId(TRAINER_ID)
                .name(NAME)
                .type(new TrainingType(TYPE))
                .date(DATE)
                .duration(DURATION)
                .build();
    }

    private TrainingCreateRequestDto buildCreateRequestDto() {
        TrainingCreateRequestDto dto = new TrainingCreateRequestDto();
        dto.setTraineeId(TRAINEE_ID);
        dto.setTrainerId(TRAINER_ID);
        dto.setName(NAME);
        dto.setType(new TrainingType(TYPE));
        dto.setDate(DATE);
        dto.setDuration(DURATION);
        return dto;
    }

    @Test
    void createTraining_mapsDtoAndCreatesTraining_returnsTraining() {
        when(trainingMapper.toEntity(createRequestDto)).thenReturn(expected);
        when(trainingDao.create(expected)).thenReturn(expected);

        Training actual = service.createTraining(createRequestDto);

        assertEquals(ID, actual.getId());
        assertEquals(TRAINEE_ID, actual.getTraineeId());
        assertEquals(TRAINER_ID, actual.getTrainerId());
        assertEquals(NAME, actual.getName());
        assertEquals(TYPE, actual.getType().getName());
        assertEquals(DATE, actual.getDate());
        assertEquals(DURATION, actual.getDuration());

        verify(trainingMapper).toEntity(createRequestDto);
        verify(trainingDao).create(expected);
    }

    @Test
    void getTraining_whenTrainingExists_returnsTraining() {
        when(trainingDao.get(ID)).thenReturn(Optional.of(expected));

        Training actual = service.getTraining(ID);

        assertEquals(ID, actual.getId());
        assertEquals(TRAINEE_ID, actual.getTraineeId());
        assertEquals(TRAINER_ID, actual.getTrainerId());
        assertEquals(NAME, actual.getName());
        assertEquals(TYPE, actual.getType().getName());
        assertEquals(DATE, actual.getDate());
        assertEquals(DURATION, actual.getDuration());

        verify(trainingDao).get(ID);
    }

    @Test
    void getTraining_whenTrainingDoesNotExist_throwsServiceException() {
        when(trainingDao.get(ID)).thenReturn(Optional.empty());

        ServiceException ex = assertThrows(ServiceException.class, () -> service.getTraining(ID));
        assertEquals("Training with id 1 not found", ex.getMessage());

        verify(trainingDao).get(ID);
    }
}