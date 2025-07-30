package com.gcs.app.repository;

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

import static org.junit.jupiter.api.Assertions.*;

@DBRider
@DBUnit(cacheConnection = true, leakHunter = true, caseSensitiveTableNames = false, schema = "PUBLIC")
@DataJpaTest
class TraineeRepositoryTest {

    private static final String USERNAME = "lionel.venture";
    private static final String TRAINING_TYPE_NAME = "Yoga";

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private TrainingTypeRepository trainingTypeRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DataSet("dataset/trainee-data.xml")
    void save_createTrainer_successfullyPersists() {
        User user = createUser("new.user", "newpass", "New");
        TrainingType specialization = trainingTypeRepository.findByName(TRAINING_TYPE_NAME).orElseThrow();

        Trainer trainer = Trainer.builder()
                .user(user)
                .specialization(specialization)
                .build();

        Trainer saved = trainerRepository.save(trainer);

        assertNotNull(saved.getId());
        assertEquals("new.user", saved.getUser().getUsername());
    }

    @Test
    @DataSet("dataset/trainee-data.xml")
    void findById_existingTrainer_returnsTrainer() {
        User user = userRepository.findByUsername(USERNAME).orElseThrow();
        TrainingType specialization = trainingTypeRepository.findByName(TRAINING_TYPE_NAME).orElseThrow();

        Trainer trainer = trainerRepository.save(Trainer.builder()
                .user(user)
                .specialization(specialization)
                .build());

        Optional<Trainer> found = trainerRepository.findById(trainer.getId());

        assertTrue(found.isPresent());
        assertEquals(USERNAME, found.get().getUser().getUsername());
    }

    @Test
    @DataSet("dataset/trainee-data.xml")
    void update_existingTrainer_updatesSuccessfully() {
        User user = createUser("update.user", "pwd", "Update");
        TrainingType oldSpec = trainingTypeRepository.findByName(TRAINING_TYPE_NAME).orElseThrow();
        TrainingType newSpec = trainingTypeRepository.save(TrainingType.builder().name("Strength").build());

        Trainer trainer = trainerRepository.save(Trainer.builder()
                .user(user)
                .specialization(oldSpec)
                .build());

        Trainer updatedTrainer = trainer.toBuilder().specialization(newSpec).build();
        Trainer saved = trainerRepository.save(updatedTrainer);

        assertEquals("Strength", saved.getSpecialization().getName());
    }

    @Test
    @DataSet("dataset/trainee-data.xml")
    void delete_existingTrainer_removesFromDb() {
        User user = createUser("delete.user", "pwd", "Delete");
        TrainingType type = trainingTypeRepository.findByName(TRAINING_TYPE_NAME).orElseThrow();

        Trainer trainer = trainerRepository.save(Trainer.builder()
                .user(user)
                .specialization(type)
                .build());

        trainerRepository.deleteById(trainer.getId());

        Optional<Trainer> deleted = trainerRepository.findById(trainer.getId());

        assertFalse(deleted.isPresent());
    }

    @Test
    @DataSet("dataset/trainee-data.xml")
    void findAll_returnsListOfTrainers() {
        List<Trainer> all = trainerRepository.findAll();

        assertFalse(all.isEmpty());
    }

    private User createUser(String username, String password, String firstName) {
        return User.builder()
                .username(username)
                .password(password)
                .firstName(firstName)
                .lastName("User")
                .isActive(true)
                .build();
    }
}