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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    private static final Trainee TRAINEE = createTrainee();
    private static final TraineeCreateRequestDto CREATE_REQUEST = createTraineeCreateRequestDto();
    private static final TraineeUpdateRequestDto UPDATE_REQUEST = createTraineeUpdateRequestDto();

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TraineeMapper traineeMapper;

    @Mock
    private UserService userService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private CredentialsService credentialsService;

    @InjectMocks
    private TraineeServiceImpl service;

    @Test
    void createTrainee_mapsDtoAndCreatesTrainee_returnsTrainee() {
        when(traineeMapper.toEntity(CREATE_REQUEST)).thenReturn(TRAINEE);
        when(userService.getAllUsernames()).thenReturn(Collections.emptySet());
        when(traineeDao.create(any(Trainee.class))).thenReturn(TRAINEE);

        Trainee actual = service.createTrainee(CREATE_REQUEST);

        assertEquals(FIRST_NAME, actual.getUser().getFirstName());
        assertEquals(LAST_NAME, actual.getUser().getLastName());
        assertTrue(actual.getUser().getIsActive());
        assertEquals(DATE_OF_BIRTH, actual.getDateOfBirth());
        assertEquals(ADDRESS, actual.getAddress());

        verify(traineeMapper).toEntity(CREATE_REQUEST);
        verify(userService).getAllUsernames();
        verify(traineeDao).create(any(Trainee.class));
    }

    @Test
    void updateTrainee_whenTraineeExists_mapsDtoUpdatesAndReturnsTrainee() {
        when(traineeDao.findByUsername(USERNAME)).thenReturn(Optional.of(TRAINEE));
        when(traineeMapper.update(TRAINEE, UPDATE_REQUEST)).thenReturn(TRAINEE);
        when(traineeDao.update(TRAINEE)).thenReturn(TRAINEE);

        Trainee actual = service.updateTrainee(UPDATE_REQUEST);

        assertEquals(FIRST_NAME, actual.getUser().getFirstName());
        assertEquals(LAST_NAME, actual.getUser().getLastName());
        assertEquals(USERNAME, actual.getUser().getUsername());
        assertTrue(actual.getUser().getIsActive());
        assertEquals(DATE_OF_BIRTH, actual.getDateOfBirth());
        assertEquals(ADDRESS, actual.getAddress());

        verify(traineeDao).findByUsername(USERNAME);
        verify(traineeMapper).update(TRAINEE, UPDATE_REQUEST);
        verify(traineeDao).update(TRAINEE);
    }

    @Test
    void updateTrainee_whenTraineeDoesNotExist_throwsServiceException() {
        when(traineeDao.findByUsername(USERNAME)).thenReturn(Optional.empty());

        ServiceException ex = assertThrows(ServiceException.class, () -> service.updateTrainee(UPDATE_REQUEST));

        assertEquals(TRAINEE_NOT_FOUND_MESSAGE, ex.getMessage());
        verify(traineeDao).findByUsername(USERNAME);
    }

    @Test
    void deleteTraineeByUsername_whenTraineeExists_deletesTrainee() {
        when(traineeDao.findByUsername(USERNAME)).thenReturn(Optional.of(TRAINEE));

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
        when(traineeDao.findByUsername(USERNAME)).thenReturn(Optional.of(TRAINEE));

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
        when(traineeDao.findByUsername(USERNAME)).thenReturn(Optional.of(TRAINEE));

        service.setTraineeActivationStatus(USERNAME, false);

        ArgumentCaptor<Trainee> captor = ArgumentCaptor.forClass(Trainee.class);
        verify(traineeDao).update(captor.capture());

        Trainee updated = captor.getValue();
        assertFalse(updated.getUser().getIsActive());
        assertEquals(USERNAME, updated.getUser().getUsername());
        verify(traineeDao).findByUsername(USERNAME);
    }

    @Test
    void setTraineeActive_whenTraineeNotFound_throwsServiceException() {
        when(traineeDao.findByUsername(USERNAME)).thenReturn(Optional.empty());

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.setTraineeActivationStatus(USERNAME, true));

        assertEquals("Trainee not found with username: " + USERNAME, ex.getMessage());
        verify(traineeDao).findByUsername(USERNAME);
    }

    @Test
    void getUnassignedTrainers_whenTraineeExists_returnsTrainerList() {
        List<Trainer> expected = List.of(createTrainer("trainer.mock"));

        when(traineeDao.findByUsername(USERNAME)).thenReturn(Optional.of(TRAINEE));
        when(trainerService.getUnassignedForTrainee(TRAINEE)).thenReturn(expected);

        List<Trainer> result = service.getUnassignedTrainers(USERNAME);

        assertEquals(expected, result);
        verify(trainerService).getUnassignedForTrainee(TRAINEE);
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
        Trainer trainer1 = createTrainer("trainer.one").toBuilder().id(1L).build();
        Trainer trainer2 = createTrainer("trainer.two").toBuilder().id(2L).build();
        Trainer oldTrainer = createTrainer("old.trainer").toBuilder().id(3L).build();

        oldTrainer.getTrainees().add(TRAINEE);
        TRAINEE.getTrainers().add(oldTrainer);
        List<String> trainerUsernames = List.of("trainer.one", "trainer.two");

        when(traineeDao.findByUsername(USERNAME)).thenReturn(Optional.of(TRAINEE));
        when(trainerService.getByUsername("trainer.one")).thenReturn(trainer1);
        when(trainerService.getByUsername("trainer.two")).thenReturn(trainer2);
        when(traineeDao.update(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Trainee actual = service.updateTraineeTrainers(USERNAME, trainerUsernames);

        Set<Trainer> updatedTrainers = actual.getTrainers();
        assertEquals(2, updatedTrainers.size());
        assertFalse(updatedTrainers.contains(oldTrainer));
        assertTrue(updatedTrainers.contains(trainer1));
        assertTrue(updatedTrainers.contains(trainer2));

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

    private static Trainee createTrainee() {
        return Trainee.builder()
                .user(createUser())
                .dateOfBirth(DATE_OF_BIRTH)
                .address(ADDRESS)
                .build();
    }

    private static User createUser() {
        return User.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .isActive(true)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .build();
    }

    private static TraineeCreateRequestDto createTraineeCreateRequestDto() {
        TraineeCreateRequestDto dto = new TraineeCreateRequestDto();
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        dto.setDateOfBirth(DATE_OF_BIRTH);
        dto.setAddress(ADDRESS);

        return dto;
    }

    private static TraineeUpdateRequestDto createTraineeUpdateRequestDto() {
        TraineeUpdateRequestDto dto = new TraineeUpdateRequestDto();
        dto.setUsername(USERNAME);
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        dto.setDateOfBirth(DATE_OF_BIRTH);
        dto.setAddress(ADDRESS);
        dto.setIsActive(true);

        return dto;
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

}
