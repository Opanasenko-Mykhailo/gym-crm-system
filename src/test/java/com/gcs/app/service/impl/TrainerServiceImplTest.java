package com.gcs.app.service.impl;

import com.gcs.app.dao.TrainerDao;
import com.gcs.app.dao.UserDao;
import com.gcs.app.exception.ServiceException;
import com.gcs.app.facade.dto.TrainerCreateRequestDto;
import com.gcs.app.facade.dto.TrainerUpdateRequestDto;
import com.gcs.app.mapper.TrainerMapper;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.TrainingType;
import com.gcs.app.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    private static final Long USER_ID = 1L;
    private static final String FIRST_NAME = "Jane";
    private static final String LAST_NAME = "Smith";
    private static final String USERNAME = "jane.smith";
    private static final String PASSWORD = "password123";
    private static final String SPECIALIZATION = "Yoga";

    private final Trainer expected = createTrainer();
    private final TrainerCreateRequestDto createRequestDto = createTrainerCreateRequestDto();
    private final TrainerUpdateRequestDto updateRequestDto = createTrainerUpdateRequestDto();

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private UserDao userDao;

    @Mock
    private TrainerMapper trainerMapper;

    @InjectMocks
    private TrainerServiceImpl service;

    @Test
    void createTrainer_mapsDtoAndCreatesTrainer_returnsTrainer() {
        when(trainerMapper.toEntity(createRequestDto)).thenReturn(expected);
        when(userDao.findAllUsernames()).thenReturn(Collections.emptySet());
        when(trainerDao.create(any(Trainer.class))).thenReturn(expected);

        Trainer actual = service.createTrainer(createRequestDto);

        assertEquals(USER_ID, actual.getId());
        assertEquals(FIRST_NAME, actual.getUser().getFirstName());
        assertEquals(LAST_NAME, actual.getUser().getLastName());
        assertTrue(actual.getUser().getIsActive());
        assertEquals(SPECIALIZATION, actual.getSpecialization().getName());

        verify(trainerMapper).toEntity(createRequestDto);
        verify(userDao).findAllUsernames();
        verify(trainerDao).create(any(Trainer.class));
    }

    @Test
    void updateTrainer_whenTrainerExists_mapsDtoUpdatesAndReturnsTrainer() {
        when(trainerMapper.toUpdateEntity(updateRequestDto)).thenReturn(expected);
        when(trainerDao.get(USER_ID)).thenReturn(Optional.of(expected));
        when(trainerDao.update(expected)).thenReturn(expected);

        Trainer actual = service.updateTrainer(updateRequestDto);

        assertEquals(USER_ID, actual.getId());
        assertEquals(FIRST_NAME, actual.getUser().getFirstName());
        assertEquals(LAST_NAME, actual.getUser().getLastName());
        assertEquals(USERNAME, actual.getUser().getUsername());
        assertTrue(actual.getUser().getIsActive());
        assertEquals(SPECIALIZATION, actual.getSpecialization().getName());

        verify(trainerMapper).toUpdateEntity(updateRequestDto);
        verify(trainerDao).get(USER_ID);
        verify(trainerDao).update(expected);
    }

    @Test
    void updateTrainer_whenTrainerDoesNotExist_throwsServiceException() {
        when(trainerMapper.toUpdateEntity(updateRequestDto)).thenReturn(expected);
        when(trainerDao.get(USER_ID)).thenReturn(Optional.empty());

        ServiceException ex = assertThrows(ServiceException.class, () -> service.updateTrainer(updateRequestDto));

        assertEquals("Trainer with userId 1 not found", ex.getMessage());
        verify(trainerMapper).toUpdateEntity(updateRequestDto);
        verify(trainerDao).get(USER_ID);
        verify(trainerDao, times(0)).update(expected);
    }

    @Test
    void getTrainer_whenTrainerExists_returnsTrainer() {
        when(trainerDao.get(USER_ID)).thenReturn(Optional.of(expected));

        Trainer actual = service.getTrainer(USER_ID);

        assertEquals(USER_ID, actual.getId());
        assertEquals(FIRST_NAME, actual.getUser().getFirstName());
        assertEquals(LAST_NAME, actual.getUser().getLastName());
        assertEquals(USERNAME, actual.getUser().getUsername());
        assertTrue(actual.getUser().getIsActive());
        assertEquals(SPECIALIZATION, actual.getSpecialization().getName());

        verify(trainerDao).get(USER_ID);
    }

    @Test
    void getTrainer_whenTrainerDoesNotExist_throwsServiceException() {
        when(trainerDao.get(USER_ID)).thenReturn(Optional.empty());

        ServiceException ex = assertThrows(ServiceException.class, () -> service.getTrainer(USER_ID));

        assertEquals("Trainer with userId 1 not found", ex.getMessage());
        verify(trainerDao).get(USER_ID);
    }

    @Test
    void getByUsername_whenTrainerExists_returnsTrainer() {
        when(trainerDao.findByUsername(USERNAME)).thenReturn(Optional.of(expected));

        Trainer actual = service.getByUsername(USERNAME);

        assertEquals(USER_ID, actual.getId());
        assertEquals(USERNAME, actual.getUser().getUsername());

        verify(trainerDao).findByUsername(USERNAME);
    }

    @Test
    void getByUsername_whenTrainerDoesNotExist_throwsServiceException() {
        when(trainerDao.findByUsername(USERNAME)).thenReturn(Optional.empty());

        ServiceException ex = assertThrows(ServiceException.class, () -> service.getByUsername(USERNAME));
        assertEquals("Trainer not found with username: " + USERNAME, ex.getMessage());

        verify(trainerDao).findByUsername(USERNAME);
    }

    @Test
    void authenticateTrainer_whenCredentialsAreCorrect_returnsTrue() {
        when(trainerDao.findByUsername(USERNAME)).thenReturn(Optional.of(expected));

        boolean result = service.authenticateTrainer(USERNAME, PASSWORD);

        assertTrue(result);
        verify(trainerDao).findByUsername(USERNAME);
    }

    @Test
    void authenticateTrainer_whenPasswordIsIncorrect_returnsFalse() {
        when(trainerDao.findByUsername(USERNAME)).thenReturn(Optional.of(expected));

        boolean result = service.authenticateTrainer(USERNAME, "wrongPassword");

        assertFalse(result);
        verify(trainerDao).findByUsername(USERNAME);
    }

    @Test
    void authenticateTrainer_whenUserDoesNotExist_returnsFalse() {
        when(trainerDao.findByUsername(USERNAME)).thenReturn(Optional.empty());

        boolean result = service.authenticateTrainer(USERNAME, PASSWORD);

        assertFalse(result);
        verify(trainerDao).findByUsername(USERNAME);
    }

    private Trainer createTrainer() {
        return Trainer.builder()
                .id(USER_ID)
                .user(createUser())
                .specialization(createTrainingType())
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

    private TrainingType createTrainingType() {
        return TrainingType.builder()
                .name(SPECIALIZATION)
                .build();
    }

    private TrainerCreateRequestDto createTrainerCreateRequestDto() {
        TrainerCreateRequestDto dto = new TrainerCreateRequestDto();
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        dto.setSpecialization(createTrainingType());

        return dto;
    }

    private TrainerUpdateRequestDto createTrainerUpdateRequestDto() {
        TrainerUpdateRequestDto dto = new TrainerUpdateRequestDto();
        dto.setUserId(USER_ID);
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        dto.setSpecialization(createTrainingType());
        dto.setIsActive(true);

        return dto;
    }
}