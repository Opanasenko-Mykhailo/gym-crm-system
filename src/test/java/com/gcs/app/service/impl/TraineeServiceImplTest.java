package com.gcs.app.service.impl;

import com.gcs.app.dao.TraineeDao;
import com.gcs.app.exception.ServiceException;
import com.gcs.app.facade.dto.TraineeCreateRequestDto;
import com.gcs.app.facade.dto.TraineeUpdateRequestDto;
import com.gcs.app.mapper.TraineeMapper;
import com.gcs.app.model.Trainee;
import com.gcs.app.model.User;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    private static final Long USER_ID = 1L;
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String USERNAME = "john.doe";
    private static final String PASSWORD = "password123";
    private static final LocalDate DATE_OF_BIRTH = LocalDate.of(1990, 1, 1);
    private static final String ADDRESS = "123 Main St";
    private static final String TRAINEE_NOT_FOUND_MESSAGE = "Trainee with userId " + USER_ID + " not found";


    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TraineeMapper traineeMapper;

    @InjectMocks
    private TraineeServiceImpl service;

    private Trainee expectedTrainee = createTrainee();
    private TraineeCreateRequestDto createRequestDto = createTraineeCreateRequestDto();
    private TraineeUpdateRequestDto updateRequestDto = createTraineeUpdateRequestDto();

    @Test
    void createTrainee_mapsDtoAndCreatesTrainee_returnsTrainee() {
        when(traineeMapper.toEntity(createRequestDto)).thenReturn(expectedTrainee);
        when(traineeDao.getAllUsernames()).thenReturn(Collections.emptySet());
        when(traineeDao.create(any(Trainee.class))).thenReturn(expectedTrainee);

        Trainee actual = service.createTrainee(createRequestDto);

        assertEquals(USER_ID, actual.getId());
        assertEquals(FIRST_NAME, actual.getUser().getFirstName());
        assertEquals(LAST_NAME, actual.getUser().getLastName());
        assertTrue(actual.getUser().getIsActive());
        assertEquals(DATE_OF_BIRTH, actual.getDateOfBirth());
        assertEquals(ADDRESS, actual.getAddress());

        verify(traineeMapper).toEntity(createRequestDto);
        verify(traineeDao).getAllUsernames();
        verify(traineeDao).create(any(Trainee.class));
    }

    @Test
    void updateTrainee_whenTraineeExists_mapsDtoUpdatesAndReturnsTrainee() {
        when(traineeMapper.toUpdateEntity(updateRequestDto)).thenReturn(expectedTrainee);
        when(traineeDao.get(USER_ID)).thenReturn(Optional.of(expectedTrainee));
        when(traineeDao.update(expectedTrainee)).thenReturn(expectedTrainee);

        Trainee actual = service.updateTrainee(updateRequestDto);

        assertEquals(USER_ID, actual.getId());
        assertEquals(FIRST_NAME, actual.getUser().getFirstName());
        assertEquals(LAST_NAME, actual.getUser().getLastName());
        assertEquals(USERNAME, actual.getUser().getUsername());
        assertTrue(actual.getUser().getIsActive());
        assertEquals(DATE_OF_BIRTH, actual.getDateOfBirth());
        assertEquals(ADDRESS, actual.getAddress());

        verify(traineeMapper).toUpdateEntity(updateRequestDto);
        verify(traineeDao).get(USER_ID);
        verify(traineeDao).update(expectedTrainee);
    }

    @Test
    void updateTrainee_whenTraineeDoesNotExist_throwsServiceException() {
        when(traineeMapper.toUpdateEntity(updateRequestDto)).thenReturn(expectedTrainee);
        when(traineeDao.get(USER_ID)).thenReturn(Optional.empty());

        ServiceException ex = assertThrows(ServiceException.class, () -> service.updateTrainee(updateRequestDto));

        assertEquals(TRAINEE_NOT_FOUND_MESSAGE, ex.getMessage());
        verify(traineeMapper).toUpdateEntity(updateRequestDto);
        verify(traineeDao).get(USER_ID);
        verify(traineeDao, times(0)).update(expectedTrainee);
    }

    @Test
    void deleteTrainee_whenTraineeExists_deletesTrainee() {
        when(traineeDao.get(USER_ID)).thenReturn(Optional.of(expectedTrainee));

        service.deleteTrainee(USER_ID);

        verify(traineeDao).get(USER_ID);
        verify(traineeDao).delete(USER_ID);
    }

    @Test
    void deleteTrainee_whenTraineeDoesNotExist_throwsServiceException() {
        when(traineeDao.get(USER_ID)).thenReturn(Optional.empty());

        ServiceException ex = assertThrows(ServiceException.class, () -> service.deleteTrainee(USER_ID));

        assertEquals(TRAINEE_NOT_FOUND_MESSAGE, ex.getMessage());
        verify(traineeDao).get(USER_ID);
        verify(traineeDao, times(0)).delete(USER_ID);
    }

    @Test
    void getTrainee_whenTraineeExists_returnsTrainee() {
        when(traineeDao.get(USER_ID)).thenReturn(Optional.of(expectedTrainee));

        Trainee actual = service.getTrainee(USER_ID);

        assertEquals(USER_ID, actual.getId());
        assertEquals(FIRST_NAME, actual.getUser().getFirstName());
        assertEquals(LAST_NAME, actual.getUser().getLastName());
        assertEquals(USERNAME, actual.getUser().getUsername());
        assertTrue(actual.getUser().getIsActive());
        assertEquals(DATE_OF_BIRTH, actual.getDateOfBirth());
        assertEquals(ADDRESS, actual.getAddress());

        verify(traineeDao).get(USER_ID);
    }

    @Test
    void getTrainee_whenTraineeDoesNotExist_throwsServiceException() {
        when(traineeDao.get(USER_ID)).thenReturn(Optional.empty());

        ServiceException ex = assertThrows(ServiceException.class, () -> service.getTrainee(USER_ID));
        assertEquals(TRAINEE_NOT_FOUND_MESSAGE, ex.getMessage());

        verify(traineeDao).get(USER_ID);
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

    private TraineeCreateRequestDto createTraineeCreateRequestDto() {
        TraineeCreateRequestDto dto = new TraineeCreateRequestDto();
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        dto.setDateOfBirth(DATE_OF_BIRTH);
        dto.setAddress(ADDRESS);

        return dto;
    }

    private TraineeUpdateRequestDto createTraineeUpdateRequestDto() {
        TraineeUpdateRequestDto dto = new TraineeUpdateRequestDto();
        dto.setUserId(USER_ID);
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        dto.setDateOfBirth(DATE_OF_BIRTH);
        dto.setAddress(ADDRESS);
        dto.setIsActive(true);

        return dto;
    }
}