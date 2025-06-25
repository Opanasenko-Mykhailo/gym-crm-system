package com.gcs.app.dao.impl;

import com.gcs.app.dao.TrainingDao;
import com.gcs.app.model.Training;
import com.gcs.app.model.enums.EntityType;
import com.gcs.app.storage.StorageGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class TrainingDaoImpl implements TrainingDao {

    private final StorageGateway storage;

    @Override
    public Training create(Training training) {
        Long id = storage.save(EntityType.TRAINING, training);
        training.setId(id);
        log.info("Created training with id: {}", id);

        return training;
    }

    @Override
    public Optional<Training> get(Long id) {
        return storage.find(EntityType.TRAINING, id, Training.class);
    }

    @Override
    public List<Training> getAll() {
        List<Training> trainings = storage.findAll(EntityType.TRAINING, Training.class);
        log.debug("Retrieved {} trainings", trainings.size());

        return trainings;
    }
}
