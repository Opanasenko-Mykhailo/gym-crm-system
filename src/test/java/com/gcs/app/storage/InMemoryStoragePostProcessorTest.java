package com.gcs.app.storage;

import com.gcs.app.model.Trainee;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.Training;
import com.gcs.app.model.TrainingType;
import com.gcs.app.model.User;
import com.gcs.app.model.enums.EntityType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gcs.app.model.enums.EntityType.TRAINEE;
import static com.gcs.app.model.enums.EntityType.TRAINER;
import static com.gcs.app.model.enums.EntityType.TRAINING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class InMemoryStoragePostProcessorTest {
    private InMemoryStoragePostProcessor postProcessor;
    private ApplicationContext mockContext;
    private DataInitializer mockInitializer;
    private InMemoryStorage storage;

    @BeforeEach
    void setUp() {
        mockContext = Mockito.mock(ApplicationContext.class);
        mockInitializer = Mockito.mock(DataInitializer.class);

        postProcessor = new InMemoryStoragePostProcessor();
        postProcessor.setApplicationContext(mockContext);

        storage = new InMemoryStorage(new HashMap<>(), new HashMap<>(), new HashMap<>());
    }

    @Test
    void testPostProcessorPopulatesInMemoryStorage() {
        Map<EntityType, List<Object>> mockData = Map.of(
                TRAINEE, List.of(createTrainee(null, "Anna", "Nowak", "anna")),
                TRAINER, List.of(createTrainer(null, "Jan", "Kowalski", "jan", "Yoga")),
                TRAINING, List.of(createTraining(null, 1L, 1L, "Session 1", "Cardio"))
        );

        when(mockContext.getBean(DataInitializer.class)).thenReturn(mockInitializer);
        when(mockInitializer.initializeData()).thenReturn(mockData);

        Object result = postProcessor.postProcessAfterInitialization(storage, "inMemoryStorage");

        assertSame(storage, result);

        Trainee trainee = (Trainee) storage.getAll(TRAINEE).get(0);
        assertEquals("Anna", trainee.getUser().getFirstName());
        assertNotNull(trainee.getId());

        Trainer trainer = (Trainer) storage.getAll(TRAINER).get(0);
        assertEquals("Jan", trainer.getUser().getFirstName());
        assertEquals("Yoga", trainer.getSpecialization().getName());

        Training training = (Training) storage.getAll(TRAINING).get(0);
        assertEquals("Session 1", training.getName());
        assertEquals("Cardio", training.getType().getName());
        assertNotNull(training.getId());
    }

    @Test
    void testPostProcessorSkipsNonStorageBean() {
        Object result = postProcessor.postProcessAfterInitialization(new Object(), "nonStorage");
        assertTrue(result instanceof Object);
    }

    private Trainee createTrainee(Long userId, String firstName, String lastName, String username) {
        return Trainee.builder()
                .id(userId)
                .user(createUser(firstName, lastName, username))
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("Test Address")
                .build();
    }

    private Trainer createTrainer(Long userId, String firstName, String lastName, String username, String specialization) {
        return Trainer.builder()
                .id(userId)
                .user(createUser(firstName, lastName, username))
                .specialization(createTrainingType(specialization))
                .build();
    }

    private Training createTraining(Long id, Long traineeId, Long trainerId, String name, String type) {
        return Training.builder()
                .id(id)
                .trainee(createTrainee(traineeId))
                .trainer(createTrainer(trainerId))
                .name(name)
                .type(createTrainingType(type))
                .date(LocalDate.of(2024, 6, 1))
                .duration(60.00)
                .build();
    }

    private User createUser(String firstName, String lastName, String username) {
        return User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .password("secure")
                .isActive(true)
                .build();
    }

    private TrainingType createTrainingType(String type) {
        return TrainingType.builder()
                .name(type)
                .build();
    }

    private Trainee createTrainee(Long traineeId) {
        return Trainee.builder()
                .id(traineeId)
                .build();
    }

    private Trainer createTrainer(Long trainerId) {
        return Trainer.builder()
                .id(trainerId)
                .build();
    }
}