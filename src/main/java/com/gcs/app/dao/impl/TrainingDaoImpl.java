package com.gcs.app.dao.impl;

import com.gcs.app.dao.TrainingDao;
import com.gcs.app.model.Training;
import com.gcs.app.storage.InMemoryStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.gcs.app.model.enums.EntityType.TRAINING;

@Repository
@Slf4j
@RequiredArgsConstructor
public class TrainingDaoImpl implements TrainingDao {

    private final InMemoryStorage storage;

    @Override
    public Training create(Training training) {
        Long id = storage.nextId();
        Training trainingWithId = training.toBuilder().trainerId(id).build();

        storage.put(TRAINING, id, trainingWithId);
        log.info("Created training with id: {}", id);

        return training;
    }

    @Override
    public Optional<Training> get(Long id) {
        Optional<Training> training = storage.getById(TRAINING, id);
        log.debug("Retrieved training with id: {}, found: {}", id, training.isPresent());

        return training;
    }
}