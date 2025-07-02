package com.gcs.app.service.impl;

import com.gcs.app.dao.TrainerDao;
import com.gcs.app.exception.ServiceException;
import com.gcs.app.facade.dto.TrainerCreateRequestDto;
import com.gcs.app.facade.dto.TrainerUpdateRequestDto;
import com.gcs.app.mapper.TrainerMapper;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.TrainingType;
import com.gcs.app.model.User;
import org.junit.jupiter.api.BeforeEach;
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

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private TrainerMapper trainerMapper;

    @InjectMocks
    private TrainerServiceImpl service;

    private Trainer expected;
    private TrainerCreateRequestDto createRequestDto;
    private TrainerUpdateRequestDto updateRequestDto;

    @BeforeEach
    void setUp() {
        expected = buildTrainer();
        createRequestDto = buildCreateRequestDto();
        updateRequestDto = buildUpdateRequestDto();
    }

    @Test
    void createTrainer_mapsDtoAndCreatesTrainer_returnsTrainer() {
        when(trainerMapper.toEntity(createRequestDto)).thenReturn(expected);
        when(trainerDao.getAllUsernames()).thenReturn(Collections.emptySet());
        when(trainerDao.create(any(Trainer.class))).thenReturn(expected);

        Trainer actual = service.createTrainer(createRequestDto);

        assertEquals(USER_ID, actual.getId());
        assertEquals(FIRST_NAME, actual.getUser().getFirstName());
        assertEquals(LAST_NAME, actual.getUser().getLastName());
        assertTrue(actual.getUser().getIsActive());
        assertEquals(SPECIALIZATION, actual.getSpecialization().getName());

        verify(trainerMapper).toEntity(createRequestDto);
        verify(trainerDao).getAllUsernames();
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

    private Trainer buildTrainer() {
        return Trainer.builder()
                .id(USER_ID)
                .user(User.builder()
                        .username(USERNAME)
                        .password(PASSWORD)
                        .isActive(true)
                        .firstName(FIRST_NAME)
                        .lastName(LAST_NAME)
                        .build())
                .specialization(TrainingType.builder()
                        .name(SPECIALIZATION)
                        .build())
                .build();
    }

    private TrainerCreateRequestDto buildCreateRequestDto() {
        TrainerCreateRequestDto dto = new TrainerCreateRequestDto();
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        dto.setSpecialization(TrainingType.builder()
                .name(SPECIALIZATION)
                .build());

        return dto;
    }

    private TrainerUpdateRequestDto buildUpdateRequestDto() {
        TrainerUpdateRequestDto dto = new TrainerUpdateRequestDto();
        dto.setUserId(USER_ID);
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        dto.setSpecialization(TrainingType.builder()
                .name(SPECIALIZATION)
                .build());
        dto.setIsActive(true);

        return dto;
    }
}