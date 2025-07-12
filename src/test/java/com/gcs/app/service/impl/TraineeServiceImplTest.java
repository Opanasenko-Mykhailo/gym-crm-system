package com.gcs.app.service.impl;

import com.gcs.app.dao.TraineeDao;
import com.gcs.app.exception.ServiceException;
import com.gcs.app.facade.dto.TraineeCreateRequestDto;
import com.gcs.app.facade.dto.TraineeUpdateRequestDto;
import com.gcs.app.mapper.TraineeMapper;
import com.gcs.app.model.Trainee;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.User;
import com.gcs.app.service.TrainerService;
import com.gcs.app.service.UserService;
import com.gcs.app.service.common.CredentialsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    private TraineeMapper traineeMapper;

    @InjectMocks
    private TraineeServiceImpl service;

    @Mock
    private UserService userService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private CredentialsService credentialsService;

    @Test
    void createTrainee_mapsDtoAndCreatesTrainee_returnsTrainee() {
        when(traineeMapper.toEntity(createRequestDto)).thenReturn(expectedTrainee);
        when(userService.getAllUsernames()).thenReturn(Collections.emptySet());
        when(traineeDao.create(any(Trainee.class))).thenReturn(expectedTrainee);

        Trainee actual = service.createTrainee(createRequestDto);

        assertEquals(FIRST_NAME, actual.getUser().getFirstName());
        assertEquals(LAST_NAME, actual.getUser().getLastName());
        assertTrue(actual.getUser().getIsActive());
        assertEquals(DATE_OF_BIRTH, actual.getDateOfBirth());
        assertEquals(ADDRESS, actual.getAddress());

        verify(traineeMapper).toEntity(createRequestDto);
        verify(userService).getAllUsernames();
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
    void setTraineeActive_whenTraineeExists_updatesIsActiveAndReturnsTrainee() {
        when(traineeDao.findByUsername(USERNAME)).thenReturn(Optional.of(expectedTrainee));
        when(traineeDao.update(any())).thenAnswer(inv -> inv.getArgument(0));

        Trainee result = service.setTraineeActive(USERNAME, false);

        assertEquals(USERNAME, result.getUser().getUsername());
        assertFalse(result.getUser().getIsActive());
        verify(traineeDao).update(any(Trainee.class));
    }

    @Test
    void setTraineeActive_whenTraineeNotFound_throwsServiceException() {
        when(traineeDao.findByUsername(USERNAME)).thenReturn(Optional.empty());

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.setTraineeActive(USERNAME, true));

        assertEquals("Trainee not found with username: " + USERNAME, ex.getMessage());
        verify(traineeDao).findByUsername(USERNAME);
    }

    @Test
    void getUnassignedTrainers_whenTraineeExists_returnsTrainerList() {
        when(traineeDao.findByUsername(USERNAME)).thenReturn(Optional.of(expectedTrainee));
        List<Trainer> expected = List.of(createTrainer("trainer.mock"));
        when(trainerService.getUnassignedForTrainee(expectedTrainee)).thenReturn(expected);

        List<Trainer> result = service.getUnassignedTrainers(USERNAME);

        assertEquals(expected, result);
        verify(trainerService).getUnassignedForTrainee(expectedTrainee);
    }

    @Test
    void getUnassignedTrainers_whenTraineeNotFound_throwsServiceException() {
        when(traineeDao.findByUsername(USERNAME)).thenReturn(Optional.empty());

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.getUnassignedTrainers(USERNAME));

        assertEquals("Trainee not found with username: " + USERNAME, ex.getMessage());
    }

    @Test
    void updateTraineeTrainers_whenTraineeExists_assignsNewTrainers() {
        Trainer trainer1 = createTrainer("trainer.one");
        Trainer trainer2 = createTrainer("trainer.two");

        Trainer oldTrainer = createTrainer("old.trainer");
        oldTrainer.getTrainees().add(expectedTrainee);
        expectedTrainee.getTrainers().add(oldTrainer);

        when(traineeDao.findByUsername(USERNAME)).thenReturn(Optional.of(expectedTrainee));
        when(trainerService.getByUsername("trainer.one")).thenReturn(trainer1);
        when(trainerService.getByUsername("trainer.two")).thenReturn(trainer2);
        when(traineeDao.update(any())).thenAnswer(invocation -> invocation.getArgument(0));

        List<String> trainerUsernames = List.of("trainer.one", "trainer.two");
        Trainee result = service.updateTraineeTrainers(USERNAME, trainerUsernames);

        assertFalse(result.getTrainers().stream()
                .anyMatch(t -> t.getUser().getUsername().equals("old.trainer")));

        List<String> resultUsernames = result.getTrainers().stream()
                .map(t -> t.getUser().getUsername())
                .toList();

        assertEquals(2, resultUsernames.size());
        assertTrue(resultUsernames.contains("trainer.one"));
        assertTrue(resultUsernames.contains("trainer.two"));

        verify(traineeDao).findByUsername(USERNAME);
        verify(trainerService).getByUsername("trainer.one");
        verify(trainerService).getByUsername("trainer.two");
        verify(traineeDao).update(any());
    }

    @Test
    void updateTraineeTrainers_whenTraineeNotFound_throwsServiceException() {
        when(traineeDao.findByUsername(USERNAME)).thenReturn(Optional.empty());

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.updateTraineeTrainers(USERNAME, List.of("trainer1")));

        assertEquals("Trainee not found with username: " + USERNAME, ex.getMessage());
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

    private Trainer createTrainer(String username) {
        User user = User.builder()
                .username(username)
                .firstName("Trainer")
                .lastName("Test")
                .isActive(true)
                .build();

        return Trainer.builder()
                .user(user)
                .trainees(new HashSet<>())
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
