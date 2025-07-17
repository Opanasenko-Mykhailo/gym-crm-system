package com.gcs.app.facade;

import com.gcs.app.rest.AuthResponse;
import com.gcs.app.rest.TrainerProfileResponse;
import com.gcs.app.rest.TrainerRegistrationRequest;
import com.gcs.app.rest.TrainerUpdateRequest;
import com.gcs.app.rest.TrainingResponse;
import com.gcs.app.facade.dto.AuthRequestDto;
import com.gcs.app.facade.dto.AuthResponseDto;
import com.gcs.app.facade.dto.PasswordChangeRequestDto;
import com.gcs.app.facade.dto.TraineeCreateRequestDto;
import com.gcs.app.facade.dto.TraineeResponseDto;
import com.gcs.app.facade.dto.TraineeTrainingSearchCriteriaDto;
import com.gcs.app.facade.dto.TraineeUpdateRequestDto;
import com.gcs.app.facade.dto.TrainerCreateRequestDto;
import com.gcs.app.facade.dto.TrainerResponseDto;
import com.gcs.app.facade.dto.TrainerTrainingSearchCriteriaDto;
import com.gcs.app.facade.dto.TrainerUpdateRequestDto;
import com.gcs.app.facade.dto.TrainingCreateRequestDto;
import com.gcs.app.facade.dto.TrainingResponseDto;
import com.gcs.app.mapper.TraineeMapper;
import com.gcs.app.mapper.TrainerMapper;
import com.gcs.app.mapper.TrainingMapper;
import com.gcs.app.model.Trainee;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.Training;
import com.gcs.app.model.TrainingType;
import com.gcs.app.model.User;
import com.gcs.app.service.TraineeService;
import com.gcs.app.service.TrainerService;
import com.gcs.app.service.TrainingService;
import com.gcs.app.service.UserService;
import com.gcs.app.service.common.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GymFacadeTest {

    private static final Long TRAINEE_ID = 1L;
    private static final String TRAINEE_FIRST_NAME = "John";
    private static final String TRAINEE_LAST_NAME = "Doe";
    private static final String TRAINEE_USERNAME = "john.doe";
    private static final LocalDate TRAINEE_DATE_OF_BIRTH = LocalDate.of(1990, 1, 1);
    private static final String TRAINEE_ADDRESS = "123 Main St";

    private static final Long TRAINER_ID = 2L;
    private static final String TRAINER_FIRST_NAME = "Jane";
    private static final String TRAINER_LAST_NAME = "Smith";
    private static final String TRAINER_USERNAME = "jane.smith";
    private static final String SPECIALIZATION = "Yoga";

    private static final Long TRAINING_ID = 1L;
    private static final String TRAINING_NAME = "Yoga Session";
    private static final LocalDate TRAINING_DATE = LocalDate.of(2025, 6, 30);
    private static final Long TRAINING_DURATION = 60L;

    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainerService trainerService;
    @Mock
    private TrainingService trainingService;
    @Mock
    private UserService userService;
    @Mock
    private AuthService authService;
    @Mock
    private TraineeMapper traineeMapper;
    @Mock
    private TrainerMapper trainerMapper;
    @Mock
    private TrainingMapper trainingMapper;

    @InjectMocks
    private GymFacade facade;

    private Trainee trainee = createTrainee();
    private Trainer trainer = createTrainer();
    private Training training = createTraining();
    private TraineeCreateRequestDto traineeCreateRequestDto = createTraineeCreateRequestDto();
    private TraineeUpdateRequestDto traineeUpdateRequestDto = createTraineeUpdateRequestDto();
    private TraineeResponseDto expectedTraineeResponse = createTraineeResponseDto();
    private TrainerResponseDto expectedTrainerResponse = createTrainerResponseDto();
    private TrainingCreateRequestDto trainingCreateRequestDto = createTrainingCreateRequestDto();
    private TrainingResponseDto expectedTrainingResponse = createTrainingResponseDto();

    @Test
    void createTrainee_callsServiceAndMapper_returnsTraineeResponseDto() {
        when(traineeService.createTrainee(traineeCreateRequestDto)).thenReturn(trainee);
        when(traineeMapper.toDto(trainee)).thenReturn(expectedTraineeResponse);

        TraineeResponseDto actual = facade.createTrainee(traineeCreateRequestDto);

        assertEquals(TRAINEE_ID, actual.getUserId());
        assertEquals(TRAINEE_FIRST_NAME, actual.getFirstName());
        assertEquals(TRAINEE_LAST_NAME, actual.getLastName());
        assertEquals(TRAINEE_USERNAME, actual.getUsername());
        assertTrue(actual.getIsActive());
        assertEquals(TRAINEE_DATE_OF_BIRTH, actual.getDateOfBirth());
        assertEquals(TRAINEE_ADDRESS, actual.getAddress());

        verify(traineeService).createTrainee(traineeCreateRequestDto);
        verify(traineeMapper).toDto(trainee);
    }

    @Test
    void updateTrainee_callsServiceAndMapper_returnsTraineeResponseDto() {
        when(traineeService.updateTrainee(traineeUpdateRequestDto)).thenReturn(trainee);
        when(traineeMapper.toDto(trainee)).thenReturn(expectedTraineeResponse);

        TraineeResponseDto actual = facade.updateTrainee(traineeUpdateRequestDto);

        assertEquals(TRAINEE_ID, actual.getUserId());
        assertEquals(TRAINEE_FIRST_NAME, actual.getFirstName());
        assertEquals(TRAINEE_LAST_NAME, actual.getLastName());
        assertEquals(TRAINEE_USERNAME, actual.getUsername());
        assertTrue(actual.getIsActive());
        assertEquals(TRAINEE_DATE_OF_BIRTH, actual.getDateOfBirth());
        assertEquals(TRAINEE_ADDRESS, actual.getAddress());

        verify(traineeService).updateTrainee(traineeUpdateRequestDto);
        verify(traineeMapper).toDto(trainee);
    }

    @Test
    void deleteTraineeByUsername_callsService() {
        facade.deleteTraineeByUsername(TRAINEE_USERNAME);
        verify(traineeService).deleteTraineeByUsername(TRAINEE_USERNAME);
    }

    @Test
    void getTraineeByUsername_callsServiceAndMapper_returnsTraineeResponseDto() {
        when(traineeService.getByUsername(TRAINEE_USERNAME)).thenReturn(trainee);
        when(traineeMapper.toDto(trainee)).thenReturn(expectedTraineeResponse);

        TraineeResponseDto actual = facade.getTraineeByUsername(TRAINEE_USERNAME);

        assertEquals(TRAINEE_ID, actual.getUserId());
        assertEquals(TRAINEE_FIRST_NAME, actual.getFirstName());
        assertEquals(TRAINEE_LAST_NAME, actual.getLastName());
        assertEquals(TRAINEE_USERNAME, actual.getUsername());

        verify(traineeService).getByUsername(TRAINEE_USERNAME);
        verify(traineeMapper).toDto(trainee);
    }

    @Test
    void updateTraineeTrainers_callsServiceAndMapper_returnsTraineeResponseDto() {
        List<String> trainerUsernames = List.of(TRAINER_USERNAME);
        when(traineeService.updateTraineeTrainers(TRAINEE_USERNAME, trainerUsernames)).thenReturn(trainee);
        when(traineeMapper.toDto(trainee)).thenReturn(expectedTraineeResponse);

        TraineeResponseDto actual = facade.updateTraineeTrainers(TRAINEE_USERNAME, trainerUsernames);

        assertEquals(TRAINEE_USERNAME, actual.getUsername());
        verify(traineeService).updateTraineeTrainers(TRAINEE_USERNAME, trainerUsernames);
        verify(traineeMapper).toDto(trainee);
    }

    @Test
    void setTraineeActive_callsServiceAndLogsAction() {
        facade.setTraineeActive(TRAINEE_USERNAME, true);

        verify(traineeService).setTraineeActivationStatus(TRAINEE_USERNAME, true);
        verifyNoMoreInteractions(traineeMapper);
    }

    @Test
    void setTrainerActive_callsServiceAndLogsAction() {
        facade.setTrainerActive(TRAINER_USERNAME, true);

        verify(trainerService).setTrainerActivationStatus(TRAINER_USERNAME, true);
        verifyNoMoreInteractions(trainerMapper);
    }

    @Test
    void getUnassignedTrainers_callsServiceAndMapper_returnsListOfTrainerResponseDto() {
        List<Trainer> trainers = List.of(trainer);
        List<TrainerResponseDto> expected = List.of(expectedTrainerResponse);

        when(traineeService.getUnassignedTrainers(TRAINEE_USERNAME)).thenReturn(trainers);
        when(trainerMapper.toDto(trainer)).thenReturn(expectedTrainerResponse);

        List<TrainerResponseDto> actual = facade.getUnassignedTrainers(TRAINEE_USERNAME);

        assertEquals(1, actual.size());
        assertEquals(TRAINER_USERNAME, actual.get(0).getUsername());
        verify(traineeService).getUnassignedTrainers(TRAINEE_USERNAME);
        verify(trainerMapper).toDto(trainer);
    }

    @Test
    void createTrainer_callsServiceAndReturnsAuthResponse() {
        TrainerRegistrationRequest swaggerRequest = new TrainerRegistrationRequest();
        swaggerRequest.setFirstName(TRAINER_FIRST_NAME);
        swaggerRequest.setLastName(TRAINER_LAST_NAME);

        TrainerCreateRequestDto createRequestDto = createTrainerCreateRequestDto();

        when(trainerMapper.toCreateRequestDto(swaggerRequest)).thenReturn(createRequestDto);
        when(trainerService.createTrainer(createRequestDto)).thenReturn(trainer);

        AuthResponse actual = facade.createTrainer(swaggerRequest);

        assertEquals(TRAINER_USERNAME, actual.getUsername());
        assertEquals(trainer.getUser().getPassword(), actual.getPassword());
        verify(trainerMapper).toCreateRequestDto(swaggerRequest);
        verify(trainerService).createTrainer(createRequestDto);
    }

    @Test
    void updateTrainer_callsServiceAndMapper_returnsTrainerProfileResponse() {
        TrainerUpdateRequest swaggerRequest = new TrainerUpdateRequest();
        swaggerRequest.setFirstName(TRAINER_FIRST_NAME);
        swaggerRequest.setLastName(TRAINER_LAST_NAME);
        String username = TRAINER_USERNAME;

        TrainerUpdateRequestDto updateRequestDto = createTrainerUpdateRequestDto();
        Trainer updatedTrainer = trainer;
        TrainerProfileResponse expectedResponse = new TrainerProfileResponse();
        expectedResponse.setUsername(TRAINER_USERNAME);
        expectedResponse.setFirstName(TRAINER_FIRST_NAME);
        expectedResponse.setLastName(TRAINER_LAST_NAME);

        when(trainerMapper.toUpdateRequestDto(swaggerRequest)).thenReturn(updateRequestDto);
        when(trainerService.updateTrainer(updateRequestDto)).thenReturn(updatedTrainer);
        when(trainerMapper.toRestModel(updatedTrainer)).thenReturn(expectedResponse);

        TrainerProfileResponse actual = facade.updateTrainer(swaggerRequest, username);

        assertEquals(expectedResponse.getUsername(), actual.getUsername());
        assertEquals(expectedResponse.getFirstName(), actual.getFirstName());
        assertEquals(expectedResponse.getLastName(), actual.getLastName());

        verify(trainerMapper).toUpdateRequestDto(swaggerRequest);
        verify(trainerService).updateTrainer(updateRequestDto);
        verify(trainerMapper).toRestModel(updatedTrainer);
    }

    @Test
    void getTrainerTrainings_callsServiceAndMapper_returnsListOfTrainingResponse() {
        TrainerTrainingSearchCriteriaDto criteria = new TrainerTrainingSearchCriteriaDto();
        List<Training> trainings = List.of(training);
        Training training = createTraining();
        TrainingResponse trainingResponse = createTrainingResponse();

        when(trainerService.getTrainerTrainings(criteria)).thenReturn(trainings);
        when(trainingMapper.toRestModel(training)).thenReturn(trainingResponse);

        List<TrainingResponse> actual = facade.getTrainerTrainings(criteria);

        assertEquals(1, actual.size());
        assertEquals(trainingResponse, actual.get(0));

        verify(trainerService).getTrainerTrainings(criteria);
        verify(trainingMapper).toRestModel(training);
    }

    @Test
    void createTraining_callsServiceAndMapper_returnsTrainingResponseDto() {
        when(trainingService.createTraining(trainingCreateRequestDto)).thenReturn(training);
        when(trainingMapper.toDto(training)).thenReturn(expectedTrainingResponse);

        TrainingResponseDto actual = facade.createTraining(trainingCreateRequestDto);

        assertEquals(TRAINING_ID, actual.getId());
        assertEquals(TRAINEE_ID, actual.getTraineeId());
        assertEquals(TRAINER_ID, actual.getTrainerId());
        assertEquals(TRAINING_NAME, actual.getName());
        assertEquals(SPECIALIZATION, actual.getType().getName());
        assertEquals(TRAINING_DATE, actual.getDate());
        assertEquals(TRAINING_DURATION, actual.getDuration());

        verify(trainingService).createTraining(trainingCreateRequestDto);
        verify(trainingMapper).toDto(training);
    }

    @Test
    void getTraining_callsServiceAndMapper_returnsTrainingResponseDto() {
        when(trainingService.getTraining(TRAINING_ID)).thenReturn(training);
        when(trainingMapper.toDto(training)).thenReturn(expectedTrainingResponse);

        TrainingResponseDto actual = facade.getTraining(TRAINING_ID);

        assertEquals(TRAINING_ID, actual.getId());
        assertEquals(TRAINEE_ID, actual.getTraineeId());
        assertEquals(TRAINER_ID, actual.getTrainerId());
        assertEquals(TRAINING_NAME, actual.getName());
        assertEquals(SPECIALIZATION, actual.getType().getName());
        assertEquals(TRAINING_DATE, actual.getDate());
        assertEquals(TRAINING_DURATION, actual.getDuration());

        verify(trainingService).getTraining(TRAINING_ID);
        verify(trainingMapper).toDto(training);
    }

    @Test
    void getTraineeTrainings_callsServiceAndMapper_returnsListOfTrainingResponseDto() {
        TraineeTrainingSearchCriteriaDto criteria = new TraineeTrainingSearchCriteriaDto();
        List<Training> trainings = List.of(training);
        List<TrainingResponseDto> expected = List.of(expectedTrainingResponse);

        when(traineeService.getTraineeTrainings(criteria)).thenReturn(trainings);
        when(trainingMapper.toDto(training)).thenReturn(expectedTrainingResponse);

        List<TrainingResponseDto> actual = facade.getTraineeTrainings(criteria);

        assertEquals(1, actual.size());
        assertEquals(TRAINING_ID, actual.get(0).getId());
        verify(traineeService).getTraineeTrainings(criteria);
        verify(trainingMapper).toDto(training);
    }

    @Test
    void getTrainerByUsername_callsServiceAndMapper_returnsTrainerProfileResponse() {
        when(trainerService.getByUsername(TRAINER_USERNAME)).thenReturn(trainer);
        TrainerProfileResponse expected = new TrainerProfileResponse();
        expected.setUsername(TRAINER_USERNAME);
        expected.setFirstName(TRAINER_FIRST_NAME);
        expected.setLastName(TRAINER_LAST_NAME);
        when(trainerMapper.toRestModel(trainer)).thenReturn(expected);

        TrainerProfileResponse actual = facade.getTrainerByUsername(TRAINER_USERNAME);

        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());

        verify(trainerService).getByUsername(TRAINER_USERNAME);
        verify(trainerMapper).toRestModel(trainer);
    }

    @Test
    void authenticate_callsService_returnsResponseDto() {
        AuthRequestDto request = new AuthRequestDto();
        request.setUsername("test.user");
        request.setPassword("password123");

        AuthResponseDto expected = new AuthResponseDto();
        expected.setSuccess(true);
        expected.setMessage("Login successful");

        when(authService.authenticate(request)).thenReturn(expected);

        AuthResponseDto actual = facade.authenticate(request);

        assertTrue(actual.getSuccess());
        assertEquals("Login successful", actual.getMessage());
        verify(authService).authenticate(request);
    }

    @Test
    void changePassword_callsUserService() {
        PasswordChangeRequestDto dto = new PasswordChangeRequestDto();
        dto.setUsername("john.doe");
        dto.setOldPassword("old123");
        dto.setNewPassword("new123");

        facade.changePassword(dto);
        verify(userService).changePassword(dto);
    }

    private Trainee createTrainee() {
        return Trainee.builder()
                .id(TRAINEE_ID)
                .user(createTraineeUser())
                .dateOfBirth(TRAINEE_DATE_OF_BIRTH)
                .address(TRAINEE_ADDRESS)
                .build();
    }

    private User createTraineeUser() {
        return User.builder()
                .firstName(TRAINEE_FIRST_NAME)
                .lastName(TRAINEE_LAST_NAME)
                .username(TRAINEE_USERNAME)
                .isActive(true)
                .build();
    }

    private Trainer createTrainer() {
        return Trainer.builder()
                .id(TRAINER_ID)
                .user(createTrainerUser())
                .specialization(createTrainingType())
                .build();
    }

    private User createTrainerUser() {
        return User.builder()
                .firstName(TRAINER_FIRST_NAME)
                .lastName(TRAINER_LAST_NAME)
                .username(TRAINER_USERNAME)
                .isActive(true)
                .build();
    }

    private Training createTraining() {
        return Training.builder()
                .id(TRAINING_ID)
                .trainee(createTraineeForTraining())
                .trainer(createTrainerForTraining())
                .name(TRAINING_NAME)
                .type(createTrainingType())
                .date(TRAINING_DATE)
                .duration(TRAINING_DURATION)
                .build();
    }

    private Trainee createTraineeForTraining() {
        return Trainee.builder().id(TRAINEE_ID).build();
    }

    private Trainer createTrainerForTraining() {
        return Trainer.builder().id(TRAINER_ID).build();
    }

    private TrainingType createTrainingType() {
        return TrainingType.builder().name(SPECIALIZATION).build();
    }

    private TraineeCreateRequestDto createTraineeCreateRequestDto() {
        TraineeCreateRequestDto dto = new TraineeCreateRequestDto();
        dto.setFirstName(TRAINEE_FIRST_NAME);
        dto.setLastName(TRAINEE_LAST_NAME);
        dto.setDateOfBirth(TRAINEE_DATE_OF_BIRTH);
        dto.setAddress(TRAINEE_ADDRESS);

        return dto;
    }

    private TraineeUpdateRequestDto createTraineeUpdateRequestDto() {
        TraineeUpdateRequestDto dto = new TraineeUpdateRequestDto();
        dto.setUsername(TRAINEE_USERNAME);
        dto.setFirstName(TRAINEE_FIRST_NAME);
        dto.setLastName(TRAINEE_LAST_NAME);
        dto.setDateOfBirth(TRAINEE_DATE_OF_BIRTH);
        dto.setAddress(TRAINEE_ADDRESS);
        dto.setIsActive(true);

        return dto;
    }

    private TraineeResponseDto createTraineeResponseDto() {
        TraineeResponseDto dto = new TraineeResponseDto();
        dto.setUserId(TRAINEE_ID);
        dto.setFirstName(TRAINEE_FIRST_NAME);
        dto.setLastName(TRAINEE_LAST_NAME);
        dto.setUsername(TRAINEE_USERNAME);
        dto.setIsActive(true);
        dto.setDateOfBirth(TRAINEE_DATE_OF_BIRTH);
        dto.setAddress(TRAINEE_ADDRESS);

        return dto;
    }

    private TrainerCreateRequestDto createTrainerCreateRequestDto() {
        TrainerCreateRequestDto dto = new TrainerCreateRequestDto();
        dto.setFirstName(TRAINER_FIRST_NAME);
        dto.setLastName(TRAINER_LAST_NAME);
        dto.setSpecialization(createTrainingType());

        return dto;
    }

    private TrainerUpdateRequestDto createTrainerUpdateRequestDto() {
        TrainerUpdateRequestDto dto = new TrainerUpdateRequestDto();
        dto.setUsername(TRAINER_USERNAME);
        dto.setFirstName(TRAINER_FIRST_NAME);
        dto.setLastName(TRAINER_LAST_NAME);
        dto.setSpecialization(createTrainingType());
        dto.setIsActive(true);

        return dto;
    }

    private TrainerResponseDto createTrainerResponseDto() {
        TrainerResponseDto dto = new TrainerResponseDto();
        dto.setUserId(TRAINER_ID);
        dto.setFirstName(TRAINER_FIRST_NAME);
        dto.setLastName(TRAINER_LAST_NAME);
        dto.setUsername(TRAINER_USERNAME);
        dto.setIsActive(true);
        dto.setSpecialization(createTrainingType());

        return dto;
    }

    private TrainingCreateRequestDto createTrainingCreateRequestDto() {
        TrainingCreateRequestDto dto = new TrainingCreateRequestDto();
        dto.setTraineeUsername(TRAINEE_USERNAME);
        dto.setTrainerUsername(TRAINER_USERNAME);
        dto.setName(TRAINING_NAME);
        dto.setType(createTrainingType());
        dto.setDate(TRAINING_DATE);
        dto.setDuration(TRAINING_DURATION);

        return dto;
    }

    private TrainingResponseDto createTrainingResponseDto() {
        TrainingResponseDto dto = new TrainingResponseDto();
        dto.setId(TRAINING_ID);
        dto.setTraineeId(TRAINEE_ID);
        dto.setTrainerId(TRAINER_ID);
        dto.setName(TRAINING_NAME);
        dto.setType(createTrainingType());
        dto.setDate(TRAINING_DATE);
        dto.setDuration(TRAINING_DURATION);

        return dto;
    }

    private TrainingResponse createTrainingResponse() {
        TrainingResponse response = new TrainingResponse();
        response.setTraineeName(TRAINEE_USERNAME);
        response.setTrainingDate(TRAINING_DATE);
        response.setTrainingDuration(Math.toIntExact(TRAINING_DURATION));
        response.setTrainingName(TRAINING_NAME);

        return response;
    }

}
