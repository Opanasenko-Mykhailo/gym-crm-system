package com.gcs.app.repository;

import com.gcs.app.model.Trainee;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.TrainingType;
import com.gcs.app.model.User;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DBRider
@DBUnit(cacheConnection = true, leakHunter = true, caseSensitiveTableNames = false, schema = "PUBLIC")
@DataJpaTest
class TrainerRepositoryTest {

    private static final String EXISTING_USERNAME = "jane.smith";
    private static final String NEW_USERNAME = "alex.ivanov";

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrainingTypeRepository trainingTypeRepository;

    @Test
    @DataSet("dataset/trainer-data.xml")
    void findByUsername_existingUsername_returnsTrainer() {
        Optional<Trainer> trainer = trainerRepository.findByUsername(EXISTING_USERNAME);

        assertTrue(trainer.isPresent());
        assertEquals(EXISTING_USERNAME, trainer.get().getUser().getUsername());
        assertNotNull(trainer.get().getTrainees());
        assertFalse(trainer.get().getTrainees().isEmpty());
    }

    @Test
    @DataSet("dataset/trainer-data.xml")
    void findByUsername_nonExistingUsername_returnsEmpty() {
        Optional<Trainer> trainer = trainerRepository.findByUsername("nonexistent");
        assertFalse(trainer.isPresent());
    }

    @Test
    @DataSet("dataset/trainer-data.xml")
    void findAllNotAssignedToTrainee_returnsCorrectTrainers() {
        Trainee trainee = traineeRepository.findByUsername("sofia.melnyk")
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        List<Trainer> trainers = trainerRepository.findAllNotAssignedToTrainee(trainee);

        assertNotNull(trainers);
        trainers.forEach(t -> assertFalse(t.getTrainees().contains(trainee)));
        assertTrue(trainers.stream().noneMatch(t -> t.getTrainees().contains(trainee)));
    }

    @Test
    @DataSet("dataset/trainer-data.xml")
    void createTrainer_savesSuccessfully() {
        User user = createAndSaveUser();
        TrainingType specialization = findOrCreateTrainingType("Yoga");
        Trainer trainer = createAndSaveTrainer(user, specialization);

        assertNotNull(trainer.getId());
        assertEquals(NEW_USERNAME, trainer.getUser().getUsername());
    }

    @Test
    @DataSet("dataset/trainer-data.xml")
    void updateTrainer_specializationUpdatedSuccessfully() {
        Trainer existingTrainer = trainerRepository.findByUsername(EXISTING_USERNAME).orElseThrow();
        TrainingType newType = findOrCreateTrainingType("Pilates");

        Trainer updated = existingTrainer.toBuilder().specialization(newType).build();
        Trainer saved = trainerRepository.save(updated);

        assertEquals("Pilates", saved.getSpecialization().getName());
    }

    @Test
    @DataSet("dataset/trainer-data.xml")
    void deleteTrainer_removedFromDatabase() {
        Trainer trainer = trainerRepository.findByUsername(EXISTING_USERNAME).orElseThrow();
        trainerRepository.delete(trainer);

        Optional<Trainer> deleted = trainerRepository.findById(trainer.getId());
        assertFalse(deleted.isPresent());
    }

    @Test
    @DataSet("dataset/trainer-data.xml")
    void findAll_returnsAllTrainers() {
        List<Trainer> trainers = trainerRepository.findAll();

        assertNotNull(trainers);
        assertEquals(2, trainers.size());
    }

    private User createAndSaveUser() {
        User user = User.builder()
                .username(TrainerRepositoryTest.NEW_USERNAME)
                .password("pass")
                .firstName("Alex")
                .lastName("Ivanov")
                .isActive(true)
                .build();

        return userRepository.save(user);
    }

    private TrainingType findOrCreateTrainingType(String name) {
        return trainingTypeRepository.findByName(name)
                .orElseGet(() -> trainingTypeRepository.save(TrainingType.builder().name(name).build()));
    }

    private Trainer createAndSaveTrainer(User user, TrainingType specialization) {
        Trainer trainer = Trainer.builder()
                .user(user)
                .specialization(specialization)
                .build();

        return trainerRepository.save(trainer);
    }
}