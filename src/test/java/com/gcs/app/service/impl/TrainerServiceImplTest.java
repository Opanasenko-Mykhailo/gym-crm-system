package com.gcs.app.service.impl;

import com.gcs.app.dao.TrainerDao;
import com.gcs.app.exception.ServiceException;
import com.gcs.app.facade.dto.TrainerCreateRequestDto;
import com.gcs.app.facade.dto.TrainerTrainingSearchCriteriaDto;
import com.gcs.app.facade.dto.TrainerUpdateRequestDto;
import com.gcs.app.mapper.TrainerMapper;
import com.gcs.app.model.Trainee;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.Training;
import com.gcs.app.model.TrainingType;
import com.gcs.app.model.User;
import com.gcs.app.service.UserService;
import com.gcs.app.service.common.CredentialsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
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
class TrainerServiceImplTest {

    private static final String FIRST_NAME = "Jane";
    private static final String LAST_NAME = "Smith";
    private static final String USERNAME = "jane.smith";
    private static final String PASSWORD = "password123";
    private static final String SPECIALIZATION = "Yoga";
    private static final String TRAINER_NOT_FOUND_MESSAGE = "Trainer with username " + USERNAME + " not found";

    private final Trainer expected = createTrainer(USERNAME);
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

    @Mock
    private CredentialsService credentialsService;

    @Test
    void createTrainer_mapsDtoAndCreatesTrainer_returnsTrainer() {
        when(trainerMapper.toEntity(createRequestDto)).thenReturn(expected);
        when(userService.getAllUsernames()).thenReturn(Collections.emptySet());
        when(trainerDao.create(any(Trainer.class))).thenReturn(expected);

        Trainer actual = service.createTrainer(createRequestDto);

        assertTrainerFields(actual);

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

        assertTrainerFields(actual);

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

        assertTrainerFields(actual);

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
    void setTrainerActive_whenTrainerExists_updatesIsActiveAndReturnsTrainer() {
        User updatedUser = createUser(USERNAME, false);
        Trainer updatedTrainer = expected.toBuilder().user(updatedUser).build();

        when(trainerDao.findByUsername(USERNAME)).thenReturn(Optional.of(expected));
        when(trainerDao.update(any())).thenReturn(updatedTrainer);

        Trainer result = service.setTrainerActive(USERNAME, false);

        assertFalse(result.getUser().getIsActive());
        verify(trainerDao).findByUsername(USERNAME);
        verify(trainerDao).update(any(Trainer.class));
    }

    @Test
    void setTrainerActive_whenTrainerDoesNotExist_throwsServiceException() {
        when(trainerDao.findByUsername(USERNAME)).thenReturn(Optional.empty());

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.setTrainerActive(USERNAME, true));

        assertEquals("Trainer not found with username: " + USERNAME, ex.getMessage());
        verify(trainerDao).findByUsername(USERNAME);
    }

    @Test
    void getUnassignedForTrainee_returnsListOfUnassignedTrainers() {
        Trainee trainee = createTrainee("trainee.username");
        List<Trainer> expectedList = List.of(expected);

        when(trainerDao.findAllNotAssignedToTrainee(trainee)).thenReturn(expectedList);

        List<Trainer> actual = service.getUnassignedForTrainee(trainee);

        assertEquals(expectedList, actual);
        verify(trainerDao).findAllNotAssignedToTrainee(trainee);
    }

    @Test
    void getTrainerTrainings_returnsListOfTrainings() {
        TrainerTrainingSearchCriteriaDto criteria = new TrainerTrainingSearchCriteriaDto();
        Training training = Training.builder().id(1L).build();
        List<Training> expectedTrainings = List.of(training);

        when(trainerDao.findByTrainerCriteria(criteria)).thenReturn(expectedTrainings);

        List<Training> actual = service.getTrainerTrainings(criteria);

        assertEquals(expectedTrainings, actual);
        verify(trainerDao).findByTrainerCriteria(criteria);
    }

    private Trainer createTrainer(String username) {
        return Trainer.builder()
                .user(createUser(username, true))
                .specialization(createTrainingType(SPECIALIZATION))
                .build();
    }

    private User createUser(String username, boolean isActive) {
        return User.builder()
                .username(username)
                .password(PASSWORD)
                .isActive(isActive)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .build();
    }

    private TrainingType createTrainingType(String name) {
        return TrainingType.builder()
                .name(name)
                .build();
    }

    private Trainee createTrainee(String username) {
        return Trainee.builder()
                .user(createUser(username, true))
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("Trainee Address")
                .build();
    }

    private TrainerCreateRequestDto createTrainerCreateRequestDto() {
        TrainerCreateRequestDto dto = new TrainerCreateRequestDto();
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        dto.setSpecialization(createTrainingType(SPECIALIZATION));

        return dto;
    }

    private TrainerUpdateRequestDto createTrainerUpdateRequestDto() {
        TrainerUpdateRequestDto dto = new TrainerUpdateRequestDto();
        dto.setUsername(USERNAME);
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        dto.setSpecialization(createTrainingType(SPECIALIZATION));
        dto.setIsActive(true);

        return dto;
    }

    private void assertTrainerFields(Trainer trainer) {
        assertEquals(FIRST_NAME, trainer.getUser().getFirstName());
        assertEquals(LAST_NAME, trainer.getUser().getLastName());
        assertEquals(USERNAME, trainer.getUser().getUsername());
        assertTrue(trainer.getUser().getIsActive());
        assertEquals(SPECIALIZATION, trainer.getSpecialization().getName());
    }
}
