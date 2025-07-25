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
import com.gcs.app.service.TrainingTypeService;
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
    private static final String UPDATED_FIRST_NAME = "Janet";
    private static final String UPDATED_LAST_NAME = "Johnson";
    private static final String USERNAME = "jane.smith";
    private static final String PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "$2a$encodedPass";
    private static final String SPECIALIZATION = "Yoga";
    private static final String TRAINER_NOT_FOUND_MESSAGE = String.format("Trainer not found with username: %s", USERNAME);
    private static final Trainer TRAINER = createTrainer();
    private static final TrainerCreateRequestDto CREATE_REQUEST = createTrainerCreateRequestDto();
    private static final TrainerUpdateRequestDto UPDATE_REQUEST = createTrainerUpdateRequestDto();

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private UserService userService;

    @Mock
    private TrainingTypeService trainingTypeService;

    @Mock
    private CredentialsService credentialsService;

    @InjectMocks
    private TrainerServiceImpl service;

    @Test
    void createTrainer_mapsDtoAndCreatesTrainer_returnsTrainer() {
        when(trainerMapper.toEntity(CREATE_REQUEST)).thenReturn(TRAINER);
        when(userService.getAllUsernames()).thenReturn(Collections.emptySet());
        when(credentialsService.generateUsername(FIRST_NAME, LAST_NAME, Collections.emptySet())).thenReturn(USERNAME);
        when(credentialsService.generateRandomPassword()).thenReturn(PASSWORD);
        when(credentialsService.encodePassword(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(trainingTypeService.getByName(SPECIALIZATION)).thenReturn(createTrainingType());
        when(trainerDao.create(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainer actual = service.createTrainer(CREATE_REQUEST);

        assertEquals(FIRST_NAME, actual.getUser().getFirstName());
        assertEquals(LAST_NAME, actual.getUser().getLastName());
        assertEquals(USERNAME, actual.getUser().getUsername());
        assertEquals(PASSWORD, actual.getUser().getPassword());
        assertTrue(actual.getUser().getIsActive());
        assertEquals(SPECIALIZATION, actual.getSpecialization().getName());

        verify(trainerMapper).toEntity(CREATE_REQUEST);
        verify(userService).getAllUsernames();
        verify(credentialsService).generateUsername(FIRST_NAME, LAST_NAME, Collections.emptySet());
        verify(credentialsService).generateRandomPassword();
        verify(credentialsService).encodePassword(PASSWORD);
        verify(trainingTypeService).getByName(SPECIALIZATION);
        verify(trainerDao).create(any(Trainer.class));
    }

    @Test
    void updateTrainer_whenTrainerExists_updatesAndReturnsTrainer() {
        when(trainerDao.findByUsername(USERNAME)).thenReturn(Optional.of(TRAINER));
        when(trainingTypeService.getByName(SPECIALIZATION)).thenReturn(createTrainingType());
        when(trainerDao.update(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainer actual = service.updateTrainer(UPDATE_REQUEST);

        assertEquals(UPDATED_FIRST_NAME, actual.getUser().getFirstName());
        assertEquals(UPDATED_LAST_NAME, actual.getUser().getLastName());
        assertEquals(USERNAME, actual.getUser().getUsername());
        assertTrue(actual.getUser().getIsActive());
        assertEquals(SPECIALIZATION, actual.getSpecialization().getName());

        ArgumentCaptor<Trainer> captor = ArgumentCaptor.forClass(Trainer.class);
        verify(trainerDao).update(captor.capture());
        Trainer updatedTrainer = captor.getValue();
        assertEquals(UPDATED_FIRST_NAME, updatedTrainer.getUser().getFirstName());
        assertEquals(UPDATED_LAST_NAME, updatedTrainer.getUser().getLastName());
        assertEquals(USERNAME, updatedTrainer.getUser().getUsername());
        assertTrue(updatedTrainer.getUser().getIsActive());
        assertEquals(SPECIALIZATION, updatedTrainer.getSpecialization().getName());

        verify(trainerDao).findByUsername(USERNAME);
        verify(trainingTypeService).getByName(SPECIALIZATION);
        verify(trainerDao).update(any(Trainer.class));
    }

    @Test
    void updateTrainer_whenTrainerDoesNotExist_throwsServiceException() {
        when(trainerDao.findByUsername(USERNAME)).thenReturn(Optional.empty());

        ServiceException ex = assertThrows(ServiceException.class, () -> service.updateTrainer(UPDATE_REQUEST));

        assertEquals(TRAINER_NOT_FOUND_MESSAGE, ex.getMessage());
        verify(trainerDao).findByUsername(USERNAME);
    }

    @Test
    void getByUsername_whenTrainerExists_returnsTrainer() {
        when(trainerDao.findByUsername(USERNAME)).thenReturn(Optional.of(TRAINER));

        Trainer actual = service.getByUsername(USERNAME);

        assertTrainerFields(actual);
        verify(trainerDao).findByUsername(USERNAME);
    }

    @Test
    void getByUsername_whenTrainerDoesNotExist_throwsServiceException() {
        when(trainerDao.findByUsername(USERNAME)).thenReturn(Optional.empty());

        ServiceException ex = assertThrows(ServiceException.class, () -> service.getByUsername(USERNAME));

        assertEquals(String.format("Trainer not found with username: %s", USERNAME), ex.getMessage());
        verify(trainerDao).findByUsername(USERNAME);
    }

    @Test
    void setTrainerActive_whenTrainerExists_updatesTrainerWithNewActiveStatus() {
        when(trainerDao.findByUsername(USERNAME)).thenReturn(Optional.of(TRAINER));

        service.setTrainerActivationStatus(USERNAME, false);

        ArgumentCaptor<Trainer> captor = ArgumentCaptor.forClass(Trainer.class);
        verify(trainerDao).update(captor.capture());

        Trainer updated = captor.getValue();
        assertFalse(updated.getUser().getIsActive());
        assertEquals(USERNAME, updated.getUser().getUsername());
        verify(trainerDao).findByUsername(USERNAME);
    }

    @Test
    void setTrainerActive_whenTrainerDoesNotExist_throwsServiceException() {
        when(trainerDao.findByUsername(USERNAME)).thenReturn(Optional.empty());

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.setTrainerActivationStatus(USERNAME, true));

        assertEquals(String.format("Trainer not found with username: %s", USERNAME), ex.getMessage());
        verify(trainerDao).findByUsername(USERNAME);
    }

    @Test
    void getUnassignedForTrainee_returnsListOfUnassignedTrainers() {
        Trainee trainee = createTrainee();
        List<Trainer> TRAINERList = List.of(TRAINER);

        when(trainerDao.findAllNotAssignedToTrainee(trainee)).thenReturn(TRAINERList);

        List<Trainer> actual = service.getUnassignedForTrainee(trainee);

        assertEquals(TRAINERList, actual);
        verify(trainerDao).findAllNotAssignedToTrainee(trainee);
    }

    @Test
    void getTrainerTrainings_returnsListOfTrainings() {
        TrainerTrainingSearchCriteriaDto criteria = new TrainerTrainingSearchCriteriaDto();
        Training training = Training.builder().id(1L).build();
        List<Training> TRAINERTrainings = List.of(training);

        when(trainerDao.findByTrainerCriteria(criteria)).thenReturn(TRAINERTrainings);

        List<Training> actual = service.getTrainerTrainings(criteria);

        assertEquals(TRAINERTrainings, actual);
        verify(trainerDao).findByTrainerCriteria(criteria);
    }

    private static Trainer createTrainer() {
        return Trainer.builder()
                .user(createUser(USERNAME))
                .specialization(createTrainingType())
                .build();
    }

    private static User createUser(String username) {
        return User.builder()
                .username(username)
                .password(PASSWORD)
                .isActive(true)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .build();
    }

    private static TrainingType createTrainingType() {
        return TrainingType.builder()
                .name(SPECIALIZATION)
                .build();
    }

    private Trainee createTrainee() {
        return Trainee.builder()
                .user(createUser("trainee.username"))
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("Trainee Address")
                .build();
    }

    private static TrainerCreateRequestDto createTrainerCreateRequestDto() {
        TrainerCreateRequestDto dto = new TrainerCreateRequestDto();
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        dto.setSpecialization(createTrainingType());

        return dto;
    }

    private static TrainerUpdateRequestDto createTrainerUpdateRequestDto() {
        TrainerUpdateRequestDto dto = new TrainerUpdateRequestDto();
        dto.setUsername(USERNAME);
        dto.setFirstName(UPDATED_FIRST_NAME);
        dto.setLastName(UPDATED_LAST_NAME);
        dto.setSpecialization(createTrainingType());
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
