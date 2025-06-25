package com.gcs.app.dao.impl;

import com.gcs.app.dao.TrainingDao;
import com.gcs.app.model.Training;
import com.gcs.app.model.enums.EntityType;
import com.gcs.app.storage.InMemoryStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class TrainingDaoImpl implements TrainingDao {

    private final InMemoryStorage storage;

    @Override
    public Training create(Training training) {
        Long id = storage.nextId();
        training.setId(id);

        storage.put(EntityType.TRAINING, id, training);
        log.info("Created training with id: {}", id);

        return training;
    }

    @Override
    public Optional<Training> get(Long id) {
        Optional<Training> training = storage.getById(EntityType.TRAINING, id);
        log.debug("Retrieved training with id: {}, found: {}", id, training.isPresent());

        return training;
    }

    @Override
    public List<Training> getAll() {
        List<Training> trainings = storage.getAll(EntityType.TRAINING);
        log.debug("Retrieved {} trainings", trainings.size());

        return trainings;
    }
}