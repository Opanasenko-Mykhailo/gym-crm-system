package service;

import com.gcs.app.dao.TraineeDao;
import com.gcs.app.exception.ServiceException;
import com.gcs.app.facade.dto.TraineeCreateRequestDto;
import com.gcs.app.facade.dto.TraineeUpdateRequestDto;
import com.gcs.app.mapper.TraineeMapper;
import com.gcs.app.model.Trainee;
import com.gcs.app.service.impl.TraineeServiceImpl;
import com.gcs.app.util.UserUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TraineeServiceTest {

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TraineeMapper traineeMapper;

    @InjectMocks
    private TraineeServiceImpl sut;

    private Trainee trainee;
    private TraineeCreateRequestDto createRequestDto;
    private TraineeUpdateRequestDto updateRequestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        trainee = Trainee.builder()
                .userId(1L)
                .firstName("John")
                .lastName("Doe")
                .username("john.doe")
                .password("password123")
                .isActive(true)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .build();
        createRequestDto = new TraineeCreateRequestDto();
        createRequestDto.setFirstName("John");
        createRequestDto.setLastName("Doe");
        createRequestDto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        createRequestDto.setAddress("123 Main St");

        updateRequestDto = new TraineeUpdateRequestDto();
        updateRequestDto.setUserId(1L);
        updateRequestDto.setFirstName("John");
        updateRequestDto.setLastName("Doe");
        updateRequestDto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        updateRequestDto.setAddress("123 Main St");
        updateRequestDto.setIsActive(true);
    }

    @Test
    void createTrainee_mapsDtoSetsUsernamePasswordAndCreatesTrainee_returnsTrainee() {
        when(traineeMapper.toEntity(createRequestDto)).thenReturn(trainee);

        try (MockedStatic<UserUtils> utilities = org.mockito.Mockito.mockStatic(UserUtils.class)) {
            utilities.when(() -> UserUtils.generateUsername("John", "Doe", Collections.emptySet()))
                    .thenReturn("john.doe");
            utilities.when(UserUtils::generateRandomPassword).thenReturn("password123");
            when(traineeDao.getAllUsernames()).thenReturn(Collections.emptySet());
            when(traineeDao.create(trainee)).thenReturn(trainee);

            Trainee result = sut.createTrainee(createRequestDto);

            assertEquals(trainee, result);
            assertEquals("john.doe", result.getUsername());
            assertEquals("password123", result.getPassword());
            assertTrue(result.getIsActive());

            verify(traineeMapper, times(1)).toEntity(createRequestDto);
            verify(traineeDao, times(1)).getAllUsernames();
            verify(traineeDao, times(1)).create(trainee);
        }
    }

    @Test
    void updateTrainee_whenTraineeExists_mapsDtoUpdatesAndReturnsTrainee() {
        when(traineeMapper.toUpdateEntity(updateRequestDto)).thenReturn(trainee);
        when(traineeDao.get(1L)).thenReturn(Optional.of(trainee));
        when(traineeDao.update(trainee)).thenReturn(trainee);

        Trainee result = sut.updateTrainee(updateRequestDto);

        assertEquals(trainee, result);
        verify(traineeMapper, times(1)).toUpdateEntity(updateRequestDto);
        verify(traineeDao, times(1)).get(1L);
        verify(traineeDao, times(1)).update(trainee);
    }

    @Test
    void updateTrainee_whenTraineeDoesNotExist_throwsServiceException() {
        when(traineeMapper.toUpdateEntity(updateRequestDto)).thenReturn(trainee);
        when(traineeDao.get(1L)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> sut.updateTrainee(updateRequestDto));
        assertEquals("Trainee with userId 1 not found", exception.getMessage());

        verify(traineeMapper, times(1)).toUpdateEntity(updateRequestDto);
        verify(traineeDao, times(1)).get(1L);
        verify(traineeDao, times(0)).update(trainee);
    }

    @Test
    void deleteTrainee_whenTraineeExists_deletesTrainee() {
        when(traineeDao.get(1L)).thenReturn(Optional.of(trainee));

        sut.deleteTrainee(1L);

        verify(traineeDao, times(1)).get(1L);
        verify(traineeDao, times(1)).delete(1L);
    }

    @Test
    void deleteTrainee_whenTraineeDoesNotExist_throwsServiceException() {
        when(traineeDao.get(1L)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> sut.deleteTrainee(1L));
        assertEquals("Trainee with userId 1 not found", exception.getMessage());

        verify(traineeDao, times(1)).get(1L);
        verify(traineeDao, times(0)).delete(1L);
    }

    @Test
    void getTrainee_whenTraineeExists_returnsTrainee() {
        when(traineeDao.get(1L)).thenReturn(Optional.of(trainee));

        Trainee result = sut.getTrainee(1L);

        assertEquals(trainee, result);

        verify(traineeDao, times(1)).get(1L);
    }

    @Test
    void getTrainee_whenTraineeDoesNotExist_throwsServiceException() {
        when(traineeDao.get(1L)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> sut.getTrainee(1L));
        assertEquals("Trainee with userId 1 not found", exception.getMessage());

        verify(traineeDao, times(1)).get(1L);
    }
}