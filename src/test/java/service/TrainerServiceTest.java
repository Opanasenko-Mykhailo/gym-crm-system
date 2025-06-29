package service;

import com.gcs.app.dao.TrainerDao;
import com.gcs.app.exception.ServiceException;
import com.gcs.app.facade.dto.TrainerCreateRequestDto;
import com.gcs.app.facade.dto.TrainerUpdateRequestDto;
import com.gcs.app.mapper.TrainerMapper;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.TrainingType;
import com.gcs.app.service.impl.TrainerServiceImpl;
import com.gcs.app.util.UserUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TrainerServiceTest {

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private TrainerMapper trainerMapper;

    @InjectMocks
    private TrainerServiceImpl sut;

    private Trainer trainer;
    private TrainerCreateRequestDto createRequestDto;
    private TrainerUpdateRequestDto updateRequestDto;

    @BeforeEach
    void setUp() {
        TrainingType trainingType = new TrainingType("Yoga");
        MockitoAnnotations.openMocks(this);
        trainer = Trainer.builder()
                .userId(1L)
                .firstName("Jane")
                .lastName("Smith")
                .username("jane.smith")
                .password("password123")
                .isActive(true)
                .specialization(trainingType)
                .build();
        createRequestDto = new TrainerCreateRequestDto();
        createRequestDto.setFirstName("Jane");
        createRequestDto.setLastName("Smith");
        createRequestDto.setSpecialization(trainingType);

        updateRequestDto = new TrainerUpdateRequestDto();
        updateRequestDto.setUserId(1L);
        updateRequestDto.setFirstName("Jane");
        updateRequestDto.setLastName("Smith");
        updateRequestDto.setSpecialization(trainingType);
        updateRequestDto.setIsActive(true);
    }

    @Test
    void createTrainer_mapsDtoSetsUsernamePasswordAndCreatesTrainer_returnsTrainer() {
        when(trainerMapper.toEntity(createRequestDto)).thenReturn(trainer);

        try (MockedStatic<UserUtils> utilities = org.mockito.Mockito.mockStatic(UserUtils.class)) {
            utilities.when(() -> UserUtils.generateUsername("Jane", "Smith", Collections.emptySet()))
                    .thenReturn("jane.smith");
            utilities.when(UserUtils::generateRandomPassword).thenReturn("password123");
            when(trainerDao.getAllUsernames()).thenReturn(Collections.emptySet());
            when(trainerDao.create(trainer)).thenReturn(trainer);

            Trainer result = sut.createTrainer(createRequestDto);

            assertEquals(trainer, result);
            assertEquals("jane.smith", result.getUsername());
            assertEquals("password123", result.getPassword());
            assertTrue(result.getIsActive());

            verify(trainerMapper, times(1)).toEntity(createRequestDto);
            verify(trainerDao, times(1)).getAllUsernames();
            verify(trainerDao, times(1)).create(trainer);
        }
    }

    @Test
    void updateTrainer_whenTrainerExists_mapsDtoUpdatesAndReturnsTrainer() {
        when(trainerMapper.toUpdateEntity(updateRequestDto)).thenReturn(trainer);
        when(trainerDao.get(1L)).thenReturn(Optional.of(trainer));
        when(trainerDao.update(trainer)).thenReturn(trainer);

        Trainer result = sut.updateTrainer(updateRequestDto);

        assertEquals(trainer, result);

        verify(trainerMapper, times(1)).toUpdateEntity(updateRequestDto);
        verify(trainerDao, times(1)).get(1L);
        verify(trainerDao, times(1)).update(trainer);
    }

    @Test
    void updateTrainer_whenTrainerDoesNotExist_throwsServiceException() {
        when(trainerMapper.toUpdateEntity(updateRequestDto)).thenReturn(trainer);
        when(trainerDao.get(1L)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> sut.updateTrainer(updateRequestDto));
        assertEquals("Trainer with userId 1 not found", exception.getMessage());

        verify(trainerMapper, times(1)).toUpdateEntity(updateRequestDto);
        verify(trainerDao, times(1)).get(1L);
        verify(trainerDao, times(0)).update(trainer);
    }

    @Test
    void getTrainer_whenTrainerExists_returnsTrainer() {
        when(trainerDao.get(1L)).thenReturn(Optional.of(trainer));

        Trainer result = sut.getTrainer(1L);

        assertEquals(trainer, result);

        verify(trainerDao, times(1)).get(1L);
    }

    @Test
    void getTrainer_whenTrainerDoesNotExist_throwsServiceException() {
        when(trainerDao.get(1L)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> sut.getTrainer(1L));
        assertEquals("Trainer with userId 1 not found", exception.getMessage());

        verify(trainerDao, times(1)).get(1L);
    }
}