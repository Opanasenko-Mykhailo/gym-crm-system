package com.gcs.app.storage;

import com.gcs.app.model.Trainee;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.Training;
import com.gcs.app.model.TrainingType;
import com.gcs.app.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
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

        Trainee trainee = Trainee.builder()
                .id(id)
                .user(User.builder()
                        .firstName("Anna")
                        .lastName("Nowak")
                        .username("anowak")
                        .password("pass123")
                        .isActive(true)
                        .build())
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("Warsaw")
                .build();

        storage.put(TRAINEE, id, trainee);

        Optional<Trainee> result = storage.getById(TRAINEE, id);
        assertTrue(result.isPresent());
        assertEquals("Anna", result.get().getUser().getFirstName());
        assertEquals("Warsaw", result.get().getAddress());
    }

    @Test
    void testPutAndGetTrainer() {
        Long id = storage.nextId();

        Trainer trainer = Trainer.builder()
                .id(id)
                .user(User.builder()
                        .firstName("John")
                        .lastName("Doe")
                        .username("jdoe")
                        .password("secure")
                        .isActive(true)
                        .build())
                .specialization(TrainingType.builder()
                        .name("Yoga")
                        .build())
                .build();

        storage.put(TRAINER, id, trainer);

        Optional<Trainer> result = storage.getById(TRAINER, id);
        assertTrue(result.isPresent());
        assertEquals("Yoga", result.get().getSpecialization().getName());
    }

    @Test
    void testPutAndGetTraining() {
        Long id = storage.nextId();

        Training training = Training.builder()
                .id(id)
                .trainee(Trainee.builder().id(1L).build())
                .trainer(Trainer.builder().id(2L).build())
                .name("Strength")
                .type(TrainingType.builder()
                        .name("Cardio")
                        .build())
                .date(LocalDate.of(2024, 6, 1))
                .duration(Duration.ofMinutes(60))
                .build();

        storage.put(TRAINING, id, training);

        Optional<Training> result = storage.getById(TRAINING, id);
        assertTrue(result.isPresent());
        assertEquals("Strength", result.get().getName());
        assertEquals("Cardio", result.get().getType().getName());
    }

    @Test
    void testGetAllTrainees() {
        Long id1 = storage.nextId();
        Long id2 = storage.nextId();

        storage.put(TRAINEE, id1, createTrainee(id1, "Ala"));
        storage.put(TRAINEE, id2, createTrainee(id2, "Ola"));

        List<Trainee> trainees = storage.getAll(TRAINEE);

        assertEquals(2, trainees.size());
        assertEquals("Ala", trainees.get(0).getUser().getFirstName());
        assertEquals("Ola", trainees.get(1).getUser().getFirstName());
    }

    @Test
    void testGetByIdNotFound() {
        Optional<Trainer> trainer = storage.getById(TRAINER, 999L);
        assertTrue(trainer.isEmpty());
    }

    @Test
    void testNextIdIncrements() {
        long id1 = storage.nextId();
        long id2 = storage.nextId();
        assertEquals(id1 + 1, id2);
    }

    private Trainee createTrainee(Long id, String name) {
        return Trainee.builder()
                .id(id)
                .user(User.builder()
                        .firstName(name)
                        .lastName("Test")
                        .username(name.toLowerCase())
                        .password("test")
                        .isActive(true)
                        .build())
                .dateOfBirth(LocalDate.of(2000, 2, 2))
                .address("Test Address")
                .build();
    }
}