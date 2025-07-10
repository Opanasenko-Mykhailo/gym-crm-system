package com.gcs.app.service.impl;

import com.gcs.app.dao.TrainerDao;
import com.gcs.app.exception.ServiceException;
import com.gcs.app.facade.dto.TrainerCreateRequestDto;
import com.gcs.app.facade.dto.TrainerUpdateRequestDto;
import com.gcs.app.mapper.TrainerMapper;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.TrainingType;
import com.gcs.app.model.User;
import com.gcs.app.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    private static final String FIRST_NAME = "Jane";
    private static final String LAST_NAME = "Smith";
    private static final String USERNAME = "jane.smith";
    private static final String PASSWORD = "password123";
    private static final String SPECIALIZATION = "Yoga";
    private static final String TRAINER_NOT_FOUND_MESSAGE = "Trainer with username " + USERNAME + " not found";

    private final Trainer expected = createTrainer();
    private final TrainerCreateRequestDto createRequestDto = createTrainerCreateRequestDto();
    private final TrainerUpdateRequestDto updateRequestDto = createTrainerUpdateRequestDto();

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private TrainerMapper trainerMapper;

    @InjectMocks
    private TrainerServiceImpl service;

    @Mock
    private UserService userService;

    @Test
    void createTrainer_mapsDtoAndCreatesTrainer_returnsTrainer() {
        when(trainerMapper.toEntity(createRequestDto)).thenReturn(expected);
        when(userService.getAllUsernames()).thenReturn(Collections.emptySet());
        when(trainerDao.create(any(Trainer.class))).thenReturn(expected);

        Trainer actual = service.createTrainer(createRequestDto);

        assertEquals(FIRST_NAME, actual.getUser().getFirstName());
        assertEquals(LAST_NAME, actual.getUser().getLastName());
        assertTrue(actual.getUser().getIsActive());
        assertEquals(SPECIALIZATION, actual.getSpecialization().getName());

        verify(trainerMapper).toEntity(createRequestDto);
        verify(userService).getAllUsernames();
        verify(trainerDao).create(any(Trainer.class));
    }

    @Test
    void updateTrainer_whenTrainerExists_mapsDtoUpdatesAndReturnsTrainer() {
        when(trainerDao.findByUsername(USERNAME)).thenReturn(Optional.of(expected));
        when(trainerMapper.update(expected, updateRequestDto)).thenReturn(expected);
        when(trainerDao.update(expected)).thenReturn(expected);

        Trainer actual = service.updateTrainer(updateRequestDto);

        assertEquals(FIRST_NAME, actual.getUser().getFirstName());
        assertEquals(LAST_NAME, actual.getUser().getLastName());
        assertEquals(USERNAME, actual.getUser().getUsername());
        assertTrue(actual.getUser().getIsActive());
        assertEquals(SPECIALIZATION, actual.getSpecialization().getName());

        verify(trainerDao).findByUsername(USERNAME);
        verify(trainerMapper).update(expected, updateRequestDto);
        verify(trainerDao).update(expected);
    }

    @Test
    void updateTrainer_whenTrainerDoesNotExist_throwsServiceException() {
        when(trainerDao.findByUsername(USERNAME)).thenReturn(Optional.empty());

        ServiceException ex = assertThrows(ServiceException.class, () -> service.updateTrainer(updateRequestDto));

        assertEquals(TRAINER_NOT_FOUND_MESSAGE, ex.getMessage());

        verify(trainerDao).findByUsername(USERNAME);
    }

    @Test
    void getByUsername_whenTrainerExists_returnsTrainer() {
        when(trainerDao.findByUsername(USERNAME)).thenReturn(Optional.of(expected));

        Trainer actual = service.getByUsername(USERNAME);

        assertEquals(USERNAME, actual.getUser().getUsername());
        assertEquals(FIRST_NAME, actual.getUser().getFirstName());
        assertEquals(LAST_NAME, actual.getUser().getLastName());
        assertTrue(actual.getUser().getIsActive());
        assertEquals(SPECIALIZATION, actual.getSpecialization().getName());

        verify(trainerDao).findByUsername(USERNAME);
    }

    @Test
    void getByUsername_whenTrainerDoesNotExist_throwsServiceException() {
        when(trainerDao.findByUsername(USERNAME)).thenReturn(Optional.empty());

        ServiceException ex = assertThrows(ServiceException.class, () -> service.getByUsername(USERNAME));

        assertEquals("Trainer not found with username: " + USERNAME, ex.getMessage());

        verify(trainerDao).findByUsername(USERNAME);
    }

    private Trainer createTrainer() {
        return Trainer.builder()
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
        dto.setUsername(USERNAME);
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        dto.setSpecialization(createTrainingType());
        dto.setIsActive(true);

        return dto;
    }
}
