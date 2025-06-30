package service;

import com.gcs.app.dao.TraineeDao;
import com.gcs.app.exception.ServiceException;
import com.gcs.app.facade.dto.TraineeCreateRequestDto;
import com.gcs.app.facade.dto.TraineeUpdateRequestDto;
import com.gcs.app.mapper.TraineeMapper;
import com.gcs.app.model.Trainee;
import com.gcs.app.service.impl.TraineeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    private static final Long USER_ID = 1L;
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String USERNAME = "john.doe";
    private static final String PASSWORD = "password123";
    private static final LocalDate DATE_OF_BIRTH = LocalDate.of(1990, 1, 1);
    private static final String ADDRESS = "123 Main St";

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TraineeMapper traineeMapper;

    @InjectMocks
    private TraineeServiceImpl service;

    private Trainee expected;
    private TraineeCreateRequestDto createRequestDto;
    private TraineeUpdateRequestDto updateRequestDto;

    @BeforeEach
    void setUp() {
        expected = buildTrainee();
        createRequestDto = buildCreateRequestDto();
        updateRequestDto = buildUpdateRequestDto();
    }

    private Trainee buildTrainee() {
        return Trainee.builder()
                .userId(USER_ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .password(PASSWORD)
                .isActive(true)
                .dateOfBirth(DATE_OF_BIRTH)
                .address(ADDRESS)
                .build();
    }

    private TraineeCreateRequestDto buildCreateRequestDto() {
        TraineeCreateRequestDto dto = new TraineeCreateRequestDto();
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        dto.setDateOfBirth(DATE_OF_BIRTH);
        dto.setAddress(ADDRESS);
        return dto;
    }

    private TraineeUpdateRequestDto buildUpdateRequestDto() {
        TraineeUpdateRequestDto dto = new TraineeUpdateRequestDto();
        dto.setUserId(USER_ID);
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        dto.setDateOfBirth(DATE_OF_BIRTH);
        dto.setAddress(ADDRESS);
        dto.setIsActive(true);
        return dto;
    }

    @Test
    void createTrainee_mapsDtoAndCreatesTrainee_returnsTrainee() {
        when(traineeMapper.toEntity(createRequestDto)).thenReturn(expected);
        when(traineeDao.getAllUsernames()).thenReturn(Collections.emptySet());
        when(traineeDao.create(expected)).thenReturn(expected);

        Trainee actual = service.createTrainee(createRequestDto);

        assertEquals(USER_ID, actual.getUserId());
        assertEquals(FIRST_NAME, actual.getFirstName());
        assertEquals(LAST_NAME, actual.getLastName());
        assertTrue(actual.getIsActive());
        assertEquals(DATE_OF_BIRTH, actual.getDateOfBirth());
        assertEquals(ADDRESS, actual.getAddress());

        verify(traineeMapper).toEntity(createRequestDto);
        verify(traineeDao).getAllUsernames();
        verify(traineeDao).create(expected);
    }

    @Test
    void updateTrainee_whenTraineeExists_mapsDtoUpdatesAndReturnsTrainee() {
        when(traineeMapper.toUpdateEntity(updateRequestDto)).thenReturn(expected);
        when(traineeDao.get(USER_ID)).thenReturn(Optional.of(expected));
        when(traineeDao.update(expected)).thenReturn(expected);

        Trainee actual = service.updateTrainee(updateRequestDto);

        assertEquals(USER_ID, actual.getUserId());
        assertEquals(FIRST_NAME, actual.getFirstName());
        assertEquals(LAST_NAME, actual.getLastName());
        assertEquals(USERNAME, actual.getUsername());
        assertTrue(actual.getIsActive());
        assertEquals(DATE_OF_BIRTH, actual.getDateOfBirth());
        assertEquals(ADDRESS, actual.getAddress());

        verify(traineeMapper).toUpdateEntity(updateRequestDto);
        verify(traineeDao).get(USER_ID);
        verify(traineeDao).update(expected);
    }

    @Test
    void updateTrainee_whenTraineeDoesNotExist_throwsServiceException() {
        when(traineeMapper.toUpdateEntity(updateRequestDto)).thenReturn(expected);
        when(traineeDao.get(USER_ID)).thenReturn(Optional.empty());

        ServiceException ex = assertThrows(ServiceException.class, () -> service.updateTrainee(updateRequestDto));
        assertEquals("Trainee with userId 1 not found", ex.getMessage());

        verify(traineeMapper).toUpdateEntity(updateRequestDto);
        verify(traineeDao).get(USER_ID);
        verify(traineeDao, times(0)).update(expected);
    }

    @Test
    void deleteTrainee_whenTraineeExists_deletesTrainee() {
        when(traineeDao.get(USER_ID)).thenReturn(Optional.of(expected));

        service.deleteTrainee(USER_ID);

        verify(traineeDao).get(USER_ID);
        verify(traineeDao).delete(USER_ID);
    }

    @Test
    void deleteTrainee_whenTraineeDoesNotExist_throwsServiceException() {
        when(traineeDao.get(USER_ID)).thenReturn(Optional.empty());

        ServiceException ex = assertThrows(ServiceException.class, () -> service.deleteTrainee(USER_ID));
        assertEquals("Trainee with userId 1 not found", ex.getMessage());

        verify(traineeDao).get(USER_ID);
        verify(traineeDao, times(0)).delete(USER_ID);
    }

    @Test
    void getTrainee_whenTraineeExists_returnsTrainee() {
        when(traineeDao.get(USER_ID)).thenReturn(Optional.of(expected));

        Trainee actual = service.getTrainee(USER_ID);

        assertEquals(USER_ID, actual.getUserId());
        assertEquals(FIRST_NAME, actual.getFirstName());
        assertEquals(LAST_NAME, actual.getLastName());
        assertEquals(USERNAME, actual.getUsername());
        assertTrue(actual.getIsActive());
        assertEquals(DATE_OF_BIRTH, actual.getDateOfBirth());
        assertEquals(ADDRESS, actual.getAddress());

        verify(traineeDao).get(USER_ID);
    }

    @Test
    void getTrainee_whenTraineeDoesNotExist_throwsServiceException() {
        when(traineeDao.get(USER_ID)).thenReturn(Optional.empty());

        ServiceException ex = assertThrows(ServiceException.class, () -> service.getTrainee(USER_ID));
        assertEquals("Trainee with userId 1 not found", ex.getMessage());

        verify(traineeDao).get(USER_ID);
    }
}