package com.gcs.app.storage;

import com.gcs.app.model.Trainee;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.Training;
import com.gcs.app.model.TrainingType;
import com.gcs.app.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.gcs.app.model.enums.EntityType.TRAINEE;
import static com.gcs.app.model.enums.EntityType.TRAINER;
import static com.gcs.app.model.enums.EntityType.TRAINING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryStorageTest {

    private Map<Long, Trainee> traineeMap;
    private Map<Long, Trainer> trainerMap;
    private Map<Long, Training> trainingMap;

    private InMemoryStorage storage;

    private static final String TRAINEE_FIRST_NAME = "Anna";
    private static final String TRAINEE_LAST_NAME = "Nowak";
    private static final String TRAINEE_USERNAME = "anowak";
    private static final String TRAINEE_PASSWORD = "pass123";
    private static final LocalDate TRAINEE_DATE_OF_BIRTH = LocalDate.of(2000, 1, 1);
    private static final String TRAINEE_ADDRESS = "Warsaw";

    private static final String TRAINER_FIRST_NAME = "John";
    private static final String TRAINER_LAST_NAME = "Doe";
    private static final String TRAINER_USERNAME = "jdoe";
    private static final String TRAINER_PASSWORD = "secure";
    private static final String TRAINER_SPECIALIZATION = "Yoga";

    private static final String TRAINING_NAME = "Strength";
    private static final String TRAINING_TYPE_NAME = "Cardio";
    private static final LocalDate TRAINING_DATE = LocalDate.of(2024, 6, 1);
    private static final Double TRAINING_DURATION = 60.00;
    private static final Long TRAINING_TRAINEE_ID = 1L;
    private static final Long TRAINING_TRAINER_ID = 2L;

    private static final String TEST_LAST_NAME = "Test";
    private static final String TEST_PASSWORD = "test";
    private static final LocalDate TEST_DATE_OF_BIRTH = LocalDate.of(2000, 2, 2);
    private static final String TEST_ADDRESS = "Test Address";
    private static final Long NOT_FOUND_ID = 999L;

    @BeforeEach
    void setUp() {
        traineeMap = new HashMap<>();
        trainerMap = new HashMap<>();
        trainingMap = new HashMap<>();
        storage = new InMemoryStorage(traineeMap, trainerMap, trainingMap);
    }

    @Test
    void testPutAndGetTrainee() {
        Long id = storage.nextId();
        Trainee trainee = createTraineeWithDetails(id);
        storage.put(TRAINEE, id, trainee);

        Optional<Trainee> result = storage.getById(TRAINEE, id);

        assertTrue(result.isPresent());
        assertEquals(TRAINEE_FIRST_NAME, result.get().getUser().getFirstName());
        assertEquals(TRAINEE_ADDRESS, result.get().getAddress());
    }

    @Test
    void testPutAndGetTrainer() {
        Long id = storage.nextId();
        Trainer trainer = createTrainerWithDetails(id);
        storage.put(TRAINER, id, trainer);

        Optional<Trainer> result = storage.getById(TRAINER, id);

        assertTrue(result.isPresent());
        assertEquals(TRAINER_SPECIALIZATION, result.get().getSpecialization().getName());
    }

    @Test
    void testPutAndGetTraining() {
        Long id = storage.nextId();
        Training training = createTrainingWithDetails(id);
        storage.put(TRAINING, id, training);

        Optional<Training> result = storage.getById(TRAINING, id)
                ;
        assertTrue(result.isPresent());
        assertEquals(TRAINING_NAME, result.get().getName());
        assertEquals(TRAINING_TYPE_NAME, result.get().getType().getName());
    }

    @Test
    void testGetAllTrainees() {
        Long id1 = storage.nextId();
        Long id2 = storage.nextId();
        storage.put(TRAINEE, id1, createSimpleTrainee(id1, "Ala"));
        storage.put(TRAINEE, id2, createSimpleTrainee(id2, "Ola"));

        List<Trainee> trainees = storage.getAll(TRAINEE);

        assertEquals(2, trainees.size());
        assertEquals("Ala", trainees.get(0).getUser().getFirstName());
        assertEquals("Ola", trainees.get(1).getUser().getFirstName());
    }

    @Test
    void testGetByIdNotFound() {
        Optional<Trainer> trainer = storage.getById(TRAINER, NOT_FOUND_ID);
        assertTrue(trainer.isEmpty());
    }

    @Test
    void testNextIdIncrements() {
        long id1 = storage.nextId();
        long id2 = storage.nextId();

        assertEquals(id1 + 1, id2);
    }

    private User createUser(String firstName, String lastName, String username, String password, boolean isActive) {
        return User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .password(password)
                .isActive(isActive)
                .build();
    }

    private Trainee createTraineeWithDetails(Long id) {
        User user = createUser(TRAINEE_FIRST_NAME, TRAINEE_LAST_NAME, TRAINEE_USERNAME, TRAINEE_PASSWORD, true);
        return Trainee.builder()
                .id(id)
                .user(user)
                .dateOfBirth(TRAINEE_DATE_OF_BIRTH)
                .address(TRAINEE_ADDRESS)
                .build();
    }

    private Trainee createSimpleTrainee(Long id, String firstName) {
        User user = createUser(firstName, TEST_LAST_NAME, firstName.toLowerCase(), TEST_PASSWORD, true);

        return Trainee.builder()
                .id(id)
                .user(user)
                .dateOfBirth(TEST_DATE_OF_BIRTH)
                .address(TEST_ADDRESS)
                .build();
    }

    private TrainingType createTrainingType(String name) {
        return TrainingType.builder()
                .name(name)
                .build();
    }

    private Trainer createTrainerWithDetails(Long id) {
        User user = createUser(TRAINER_FIRST_NAME, TRAINER_LAST_NAME, TRAINER_USERNAME, TRAINER_PASSWORD, true);
        TrainingType specialization = createTrainingType(TRAINER_SPECIALIZATION);

        return Trainer.builder()
                .id(id)
                .user(user)
                .specialization(specialization)
                .build();
    }

    private Training createTrainingWithDetails(Long id) {
        Trainee trainee = Trainee.builder().id(TRAINING_TRAINEE_ID).build();
        Trainer trainer = Trainer.builder().id(TRAINING_TRAINER_ID).build();
        TrainingType type = createTrainingType(TRAINING_TYPE_NAME);

        return Training.builder()
                .id(id)
                .trainee(trainee)
                .trainer(trainer)
                .name(TRAINING_NAME)
                .type(type)
                .date(TRAINING_DATE)
                .duration(TRAINING_DURATION)
                .build();
    }
}