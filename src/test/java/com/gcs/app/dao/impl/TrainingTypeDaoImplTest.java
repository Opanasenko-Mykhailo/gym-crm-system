package com.gcs.app.dao.impl;

import com.gcs.app.dao.AbstractRepositoryTest;
import com.gcs.app.model.TrainingType;
import com.github.database.rider.core.api.dataset.DataSet;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataSet(value = "dataset/training-type-data.xml", cleanBefore = true, cleanAfter = true, transactional = true)
class TrainingTypeDaoImplTest extends AbstractRepositoryTest<TrainingTypeDaoImpl> {

    private static final String TRAINING_NAME_YOGA = "Yoga";
    private static final String TRAINING_NAME_PILATES = "Pilates";

    @Test
    void findAll_shouldReturnAllTrainingTypes() {
        List<TrainingType> trainingTypes = dao.findAll();

        assertNotNull(trainingTypes);
        assertEquals(2, trainingTypes.size());
        assertEquals(TRAINING_NAME_PILATES, trainingTypes.get(0).getName());
        assertEquals(TRAINING_NAME_YOGA, trainingTypes.get(1).getName());
    }
}
