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
import java.util.stream.Collectors;

@Repository
@Slf4j
@RequiredArgsConstructor
public class TrainingDaoImpl implements TrainingDao {

    private final InMemoryStorage storage;

    @Override
    public Training create(Training training) {
        Long id = storage.getNextId();
        training.setId(id);

        storage.getNamespace(EntityType.TRAINING).put(id, training);
        log.info("Created training with id: {}", id);

        return training;
    }

    @Override
    public Optional<Training> get(Long id) {
        Training training = (Training) storage.getNamespace(EntityType.TRAINING).get(id);
        return Optional.ofNullable(training);
    }

    @Override
    public List<Training> getAll() {
        List<Training> trainings = storage.getNamespace(EntityType.TRAINING).values()
                .stream()
                .map(Training.class::cast)
                .collect(Collectors.toList());
        log.debug("Retrieved {} trainings", trainings.size());

        return trainings;
    }
}