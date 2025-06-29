package facade;

import com.gcs.app.facade.GymFacade;
import com.gcs.app.facade.dto.TraineeCreateRequestDto;
import com.gcs.app.facade.dto.TraineeResponseDto;
import com.gcs.app.facade.dto.TraineeUpdateRequestDto;
import com.gcs.app.facade.dto.TrainerCreateRequestDto;
import com.gcs.app.facade.dto.TrainerResponseDto;
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
import com.gcs.app.service.TraineeService;
import com.gcs.app.service.TrainerService;
import com.gcs.app.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GymFacadeTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    @Mock
    private TraineeMapper traineeMapper;

    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private TrainingMapper trainingMapper;

    @InjectMocks
    private GymFacade sut;

    private Trainee trainee;
    private Trainer trainer;
    private Training training;
    private TraineeCreateRequestDto traineeCreateRequestDto;
    private TraineeUpdateRequestDto traineeUpdateRequestDto;
    private TraineeResponseDto traineeResponseDto;
    private TrainerCreateRequestDto trainerCreateRequestDto;
    private TrainerUpdateRequestDto trainerUpdateRequestDto;
    private TrainerResponseDto trainerResponseDto;
    private TrainingCreateRequestDto trainingCreateRequestDto;
    private TrainingResponseDto trainingResponseDto;

    @BeforeEach
    void setUp() {
        TrainingType trainingType = new TrainingType("Yoga");
        MockitoAnnotations.openMocks(this);

        trainee = Trainee.builder()
                .userId(1L)
                .firstName("John")
                .lastName("Doe")
                .username("john.doe")
                .password("password123")
                .isActive(true)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .build();

        trainer = Trainer.builder()
                .userId(2L)
                .firstName("Jane")
                .lastName("Smith")
                .username("jane.smith")
                .password("password123")
                .isActive(true)
                .specialization(trainingType)
                .build();

        training = Training.builder()
                .id(1L)
                .traineeId(1L)
                .trainerId(2L)
                .name("Yoga Session")
                .type(trainingType)
                .date(LocalDate.of(2025, 6, 30))
                .duration(Duration.ofHours(1))
                .build();

        traineeCreateRequestDto = new TraineeCreateRequestDto();
        traineeCreateRequestDto.setFirstName("John");
        traineeCreateRequestDto.setLastName("Doe");
        traineeCreateRequestDto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        traineeCreateRequestDto.setAddress("123 Main St");

        traineeUpdateRequestDto = new TraineeUpdateRequestDto();
        traineeUpdateRequestDto.setUserId(1L);
        traineeUpdateRequestDto.setFirstName("John");
        traineeUpdateRequestDto.setLastName("Doe");
        traineeUpdateRequestDto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        traineeUpdateRequestDto.setAddress("123 Main St");
        traineeUpdateRequestDto.setIsActive(true);

        traineeResponseDto = new TraineeResponseDto();
        traineeResponseDto.setUserId(1L);
        traineeResponseDto.setFirstName("John");
        traineeResponseDto.setLastName("Doe");
        traineeResponseDto.setUsername("john.doe");
        traineeResponseDto.setIsActive(true);
        traineeResponseDto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        traineeResponseDto.setAddress("123 Main St");

        trainerCreateRequestDto = new TrainerCreateRequestDto();
        trainerCreateRequestDto.setFirstName("Jane");
        trainerCreateRequestDto.setLastName("Smith");
        trainerCreateRequestDto.setSpecialization(trainingType);

        trainerUpdateRequestDto = new TrainerUpdateRequestDto();
        trainerUpdateRequestDto.setUserId(2L);
        trainerUpdateRequestDto.setFirstName("Jane");
        trainerUpdateRequestDto.setLastName("Smith");
        trainerUpdateRequestDto.setSpecialization(trainingType);
        trainerUpdateRequestDto.setIsActive(true);

        trainerResponseDto = new TrainerResponseDto();
        trainerResponseDto.setUserId(2L);
        trainerResponseDto.setFirstName("Jane");
        trainerResponseDto.setLastName("Smith");
        trainerResponseDto.setUsername("jane.smith");
        trainerResponseDto.setIsActive(true);
        trainerResponseDto.setSpecialization(trainingType);

        trainingCreateRequestDto = new TrainingCreateRequestDto();
        trainingCreateRequestDto.setTraineeId(1L);
        trainingCreateRequestDto.setTrainerId(2L);
        trainingCreateRequestDto.setName("Yoga Session");
        trainingCreateRequestDto.setType(trainingType);
        trainingCreateRequestDto.setDate(LocalDate.of(2025, 6, 30));
        trainingCreateRequestDto.setDuration(Duration.ofHours(1));

        trainingResponseDto = new TrainingResponseDto();
        trainingResponseDto.setId(1L);
        trainingResponseDto.setTraineeId(1L);
        trainingResponseDto.setTrainerId(2L);
        trainingResponseDto.setName("Yoga Session");
        trainingResponseDto.setType(trainingType);
        trainingResponseDto.setDate(LocalDate.of(2025, 6, 30));
        trainingResponseDto.setDuration(Duration.ofHours(1));
    }

    @Test
    void createTrainee_callsServiceAndMapper_returnsTraineeResponseDto() {
        when(traineeService.createTrainee(traineeCreateRequestDto)).thenReturn(trainee);
        when(traineeMapper.toDto(trainee)).thenReturn(traineeResponseDto);

        TraineeResponseDto result = sut.createTrainee(traineeCreateRequestDto);

        assertEquals(traineeResponseDto, result);

        verify(traineeService, times(1)).createTrainee(traineeCreateRequestDto);
        verify(traineeMapper, times(1)).toDto(trainee);
    }

    @Test
    void updateTrainee_callsServiceAndMapper_returnsTraineeResponseDto() {
        when(traineeService.updateTrainee(traineeUpdateRequestDto)).thenReturn(trainee);
        when(traineeMapper.toDto(trainee)).thenReturn(traineeResponseDto);

        TraineeResponseDto result = sut.updateTrainee(traineeUpdateRequestDto);

        assertEquals(traineeResponseDto, result);

        verify(traineeService, times(1)).updateTrainee(traineeUpdateRequestDto);
        verify(traineeMapper, times(1)).toDto(trainee);
    }

    @Test
    void deleteTrainee_callsService() {
        sut.deleteTrainee(1L);

        verify(traineeService, times(1)).deleteTrainee(1L);
    }

    @Test
    void getTrainee_callsServiceAndMapper_returnsTraineeResponseDto() {
        when(traineeService.getTrainee(1L)).thenReturn(trainee);
        when(traineeMapper.toDto(trainee)).thenReturn(traineeResponseDto);

        TraineeResponseDto result = sut.getTrainee(1L);

        assertEquals(traineeResponseDto, result);

        verify(traineeService, times(1)).getTrainee(1L);
        verify(traineeMapper, times(1)).toDto(trainee);
    }

    @Test
    void createTrainer_callsServiceAndMapper_returnsTrainerResponseDto() {
        when(trainerService.createTrainer(trainerCreateRequestDto)).thenReturn(trainer);
        when(trainerMapper.toDto(trainer)).thenReturn(trainerResponseDto);

        TrainerResponseDto result = sut.createTrainer(trainerCreateRequestDto);

        assertEquals(trainerResponseDto, result);

        verify(trainerService, times(1)).createTrainer(trainerCreateRequestDto);
        verify(trainerMapper, times(1)).toDto(trainer);
    }

    @Test
    void updateTrainer_callsServiceAndMapper_returnsTrainerResponseDto() {
        when(trainerService.updateTrainer(trainerUpdateRequestDto)).thenReturn(trainer);
        when(trainerMapper.toDto(trainer)).thenReturn(trainerResponseDto);

        TrainerResponseDto result = sut.updateTrainer(trainerUpdateRequestDto);

        assertEquals(trainerResponseDto, result);

        verify(trainerService, times(1)).updateTrainer(trainerUpdateRequestDto);
        verify(trainerMapper, times(1)).toDto(trainer);
    }

    @Test
    void getTrainer_callsServiceAndMapper_returnsTrainerResponseDto() {
        when(trainerService.getTrainer(2L)).thenReturn(trainer);
        when(trainerMapper.toDto(trainer)).thenReturn(trainerResponseDto);

        TrainerResponseDto result = sut.getTrainer(2L);

        assertEquals(trainerResponseDto, result);

        verify(trainerService, times(1)).getTrainer(2L);
        verify(trainerMapper, times(1)).toDto(trainer);
    }

    @Test
    void createTraining_callsServiceAndMapper_returnsTrainingResponseDto() {
        when(trainingService.createTraining(trainingCreateRequestDto)).thenReturn(training);
        when(trainingMapper.toDto(training)).thenReturn(trainingResponseDto);

        TrainingResponseDto result = sut.createTraining(trainingCreateRequestDto);

        assertEquals(trainingResponseDto, result);
        verify(trainingService, times(1)).createTraining(trainingCreateRequestDto);
        verify(trainingMapper, times(1)).toDto(training);
    }

    @Test
    void getTraining_callsServiceAndMapper_returnsTrainingResponseDto() {
        when(trainingService.getTraining(1L)).thenReturn(training);
        when(trainingMapper.toDto(training)).thenReturn(trainingResponseDto);

        TrainingResponseDto result = sut.getTraining(1L);

        assertEquals(trainingResponseDto, result);
        verify(trainingService, times(1)).getTraining(1L);
        verify(trainingMapper, times(1)).toDto(training);
    }
}