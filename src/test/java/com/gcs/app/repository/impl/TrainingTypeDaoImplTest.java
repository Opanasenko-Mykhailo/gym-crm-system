package com.gcs.app.repository.impl;

import com.gcs.app.repository.AbstractRepositoryTest;
import com.gcs.app.model.TrainingType;
import com.github.database.rider.core.api.dataset.DataSet;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataSet(value = "dataset/training-type-data.xml", cleanBefore = true, cleanAfter = true, transactional = true)
class TrainingTypeDaoImplTest extends AbstractRepositoryTest<TrainingTypeDaoImpl> {

    private static final String TRAINING_NAME_YOGA = "Yoga";
    private static final String TRAINING_NAME_PILATES = "Pilates";
    private static final String NON_EXISTENT_NAME = "Crossfit";

    @Test
    void findAll_shouldReturnAllTrainingTypes() {
        List<TrainingType> trainingTypes = dao.findAll();

        assertNotNull(trainingTypes);
        assertEquals(2, trainingTypes.size());
        assertEquals(TRAINING_NAME_PILATES, trainingTypes.get(0).getName());
        assertEquals(TRAINING_NAME_YOGA, trainingTypes.get(1).getName());
    }

    @Test
    void findByName_shouldReturnCorrectTrainingType() {
        Optional<TrainingType> result = dao.findByName(TRAINING_NAME_YOGA);

        assertTrue(result.isPresent());
        assertEquals(TRAINING_NAME_YOGA, result.get().getName());
    }

    @Test
    void findByName_shouldReturnEmptyWhenNotFound() {
        Optional<TrainingType> result = dao.findByName(NON_EXISTENT_NAME);

        assertFalse(result.isPresent());
    }
}