package com.gcs.app.service.impl;

import com.gcs.app.dao.TraineeDao;
import com.gcs.app.dao.UserDao;
import com.gcs.app.exception.ServiceException;
import com.gcs.app.facade.dto.PasswordChangeRequestDto;
import com.gcs.app.facade.dto.TraineeCreateRequestDto;
import com.gcs.app.facade.dto.TraineeUpdateRequestDto;
import com.gcs.app.mapper.TraineeMapper;
import com.gcs.app.model.Trainee;
import com.gcs.app.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    private static final String USERNAME = "john.doe";
    private static final String PASSWORD = "password123";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final LocalDate DATE_OF_BIRTH = LocalDate.of(1990, 1, 1);
    private static final String ADDRESS = "123 Main St";
    private static final String TRAINEE_NOT_FOUND_MESSAGE = "Trainee with username " + USERNAME + " not found";

    private final Trainee expectedTrainee = createTrainee();
    private final TraineeCreateRequestDto createRequestDto = createTraineeCreateRequestDto();
    private final TraineeUpdateRequestDto updateRequestDto = createTraineeUpdateRequestDto();

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private UserDao userDao;

    @Mock
    private TraineeMapper traineeMapper;

    @InjectMocks
    private TraineeServiceImpl service;

    @Test
    void createTrainee_mapsDtoAndCreatesTrainee_returnsTrainee() {
        when(traineeMapper.toEntity(createRequestDto)).thenReturn(expectedTrainee);
        when(userDao.findAllUsernames()).thenReturn(Collections.emptySet());
        when(traineeDao.create(any(Trainee.class))).thenReturn(expectedTrainee);

        Trainee actual = service.createTrainee(createRequestDto);

        assertEquals(FIRST_NAME, actual.getUser().getFirstName());
        assertEquals(LAST_NAME, actual.getUser().getLastName());
        assertTrue(actual.getUser().getIsActive());
        assertEquals(DATE_OF_BIRTH, actual.getDateOfBirth());
        assertEquals(ADDRESS, actual.getAddress());

        verify(traineeMapper).toEntity(createRequestDto);
        verify(userDao).findAllUsernames();
        verify(traineeDao).create(any(Trainee.class));
    }

    @Test
    void updateTrainee_whenTraineeExists_mapsDtoUpdatesAndReturnsTrainee() {
        when(traineeDao.findByUsername(USERNAME)).thenReturn(Optional.of(expectedTrainee));
        when(traineeMapper.update(expectedTrainee, updateRequestDto)).thenReturn(expectedTrainee);
        when(traineeDao.update(expectedTrainee)).thenReturn(expectedTrainee);

        Trainee actual = service.updateTrainee(updateRequestDto);

        assertEquals(FIRST_NAME, actual.getUser().getFirstName());
        assertEquals(LAST_NAME, actual.getUser().getLastName());
        assertEquals(USERNAME, actual.getUser().getUsername());
        assertTrue(actual.getUser().getIsActive());
        assertEquals(DATE_OF_BIRTH, actual.getDateOfBirth());
        assertEquals(ADDRESS, actual.getAddress());

        verify(traineeDao).findByUsername(USERNAME);
        verify(traineeMapper).update(expectedTrainee, updateRequestDto);
        verify(traineeDao).update(expectedTrainee);
    }

    @Test
    void updateTrainee_whenTraineeDoesNotExist_throwsServiceException() {
        when(traineeDao.findByUsername(USERNAME)).thenReturn(Optional.empty());

        ServiceException ex = assertThrows(ServiceException.class, () -> service.updateTrainee(updateRequestDto));

        assertEquals(TRAINEE_NOT_FOUND_MESSAGE, ex.getMessage());

        verify(traineeDao).findByUsername(USERNAME);
    }

    @Test
    void deleteTraineeByUsername_whenTraineeExists_deletesTrainee() {
        when(traineeDao.findByUsername(USERNAME)).thenReturn(Optional.of(expectedTrainee));

        service.deleteTraineeByUsername(USERNAME);

        verify(traineeDao).findByUsername(USERNAME);
        verify(traineeDao).deleteByUsername(USERNAME);
    }

    @Test
    void deleteTraineeByUsername_whenTraineeDoesNotExist_throwsServiceException() {
        when(traineeDao.findByUsername(USERNAME)).thenReturn(Optional.empty());

        ServiceException ex = assertThrows(ServiceException.class, () -> service.deleteTraineeByUsername(USERNAME));

        assertEquals(TRAINEE_NOT_FOUND_MESSAGE, ex.getMessage());

        verify(traineeDao).findByUsername(USERNAME);
    }

    @Test
    void getByUsername_whenTraineeExists_returnsTrainee() {
        when(traineeDao.findByUsername(USERNAME)).thenReturn(Optional.of(expectedTrainee));

        Trainee actual = service.getByUsername(USERNAME);

        assertEquals(USERNAME, actual.getUser().getUsername());
        assertEquals(FIRST_NAME, actual.getUser().getFirstName());
        assertEquals(LAST_NAME, actual.getUser().getLastName());
        assertTrue(actual.getUser().getIsActive());
        assertEquals(DATE_OF_BIRTH, actual.getDateOfBirth());
        assertEquals(ADDRESS, actual.getAddress());

        verify(traineeDao).findByUsername(USERNAME);
    }

    @Test
    void getByUsername_whenTraineeDoesNotExist_throwsServiceException() {
        when(traineeDao.findByUsername(USERNAME)).thenReturn(Optional.empty());

        ServiceException ex = assertThrows(ServiceException.class, () -> service.getByUsername(USERNAME));

        assertEquals("Trainee not found with username: " + USERNAME, ex.getMessage());

        verify(traineeDao).findByUsername(USERNAME);
    }

    @Test
    void changePassword_whenOldPasswordMatches_updatesPassword() {
        PasswordChangeRequestDto dto = new PasswordChangeRequestDto();
        dto.setUsername(USERNAME);
        dto.setOldPassword(PASSWORD);
        dto.setNewPassword("NewPassword123!");

        when(traineeDao.findByUsername(USERNAME)).thenReturn(Optional.of(expectedTrainee));

        service.changePassword(dto);

        ArgumentCaptor<Trainee> captor = ArgumentCaptor.forClass(Trainee.class);
        verify(traineeDao).update(captor.capture());

        Trainee updatedTrainee = captor.getValue();
        assertEquals("NewPassword123!", updatedTrainee.getUser().getPassword());

        verify(traineeDao).findByUsername(USERNAME);
    }

    @Test
    void changePassword_whenOldPasswordDoesNotMatch_throwsException() {
        PasswordChangeRequestDto dto = new PasswordChangeRequestDto();
        dto.setUsername(USERNAME);
        dto.setOldPassword("wrongOld");
        dto.setNewPassword("NewPassword123!");

        when(traineeDao.findByUsername(USERNAME)).thenReturn(Optional.of(expectedTrainee));

        ServiceException exception = assertThrows(ServiceException.class, () -> service.changePassword(dto));

        assertEquals("Old password is incorrect", exception.getMessage());

        verify(traineeDao).findByUsername(USERNAME);
    }

    @Test
    void changePassword_whenTraineeNotFound_throwsException() {
        PasswordChangeRequestDto dto = new PasswordChangeRequestDto();
        dto.setUsername(USERNAME);
        dto.setOldPassword(PASSWORD);
        dto.setNewPassword("NewPassword123!");

        when(traineeDao.findByUsername(USERNAME)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> service.changePassword(dto));

        assertEquals("User not found: " + USERNAME, exception.getMessage());

        verify(traineeDao).findByUsername(USERNAME);
    }

    private Trainee createTrainee() {
        return Trainee.builder()
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
        dto.setUsername(USERNAME);
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        dto.setDateOfBirth(DATE_OF_BIRTH);
        dto.setAddress(ADDRESS);
        dto.setIsActive(true);

        return dto;
    }
}
