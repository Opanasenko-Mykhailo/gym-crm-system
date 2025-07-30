package com.gcs.app.repository;

import com.gcs.app.model.TrainingType;
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
class TrainingTypeRepositoryTest {

    @Autowired
    private TrainingTypeRepository trainingTypeRepository;

    @Test
    @DataSet(value = "dataset/training-type-data.xml", cleanBefore = true, cleanAfter = true)
    void findByName_existingType_returnsType() {
        Optional<TrainingType> result = trainingTypeRepository.findByName("Pilates");

        assertTrue(result.isPresent());
        assertEquals("Pilates", result.get().getName());
    }

    @Test
    @DataSet(value = "dataset/training-type-data.xml", cleanBefore = true, cleanAfter = true)
    void findByName_nonExistingType_returnsEmpty() {
        Optional<TrainingType> result = trainingTypeRepository.findByName("Boxing");

        assertFalse(result.isPresent());
    }

    @Test
    void save_newTrainingType_persistsSuccessfully() {
        TrainingType newType = TrainingType.builder().name("Stretching").build();

        TrainingType saved = trainingTypeRepository.save(newType);

        assertNotNull(saved.getId());
        assertEquals("Stretching", saved.getName());
    }

    @Test
    @DataSet(value = "dataset/training-type-data.xml", cleanBefore = true, cleanAfter = true)
    void findById_existingId_returnsType() {
        Optional<TrainingType> type = trainingTypeRepository.findById(1L);

        assertTrue(type.isPresent());
        assertEquals("Pilates", type.get().getName());
    }

    @Test
    void findAll_returnsAllTrainingTypes() {
        trainingTypeRepository.save(TrainingType.builder().name("Yoga").build());
        trainingTypeRepository.save(TrainingType.builder().name("HIIT").build());

        List<TrainingType> all = trainingTypeRepository.findAll();

        assertEquals(2, all.size());
    }

    @Test
    void update_existingTrainingType_updatesSuccessfully() {
        TrainingType type = trainingTypeRepository.save(TrainingType.builder().name("Zumba").build());

        TrainingType updated = type.toBuilder().name("Advanced Zumba").build();
        TrainingType savedUpdated = trainingTypeRepository.save(updated);

        assertEquals("Advanced Zumba", savedUpdated.getName());
        assertEquals(type.getId(), savedUpdated.getId());
    }

    @Test
    void deleteById_existingId_deletesSuccessfully() {
        TrainingType saved = trainingTypeRepository.save(TrainingType.builder().name("Crossfit").build());

        Long id = saved.getId();
        trainingTypeRepository.deleteById(id);

        assertFalse(trainingTypeRepository.findById(id).isPresent());
    }
}