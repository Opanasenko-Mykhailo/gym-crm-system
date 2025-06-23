package com.gcs.app.dao.impl;

import com.gcs.app.dao.TrainingDao;
import com.gcs.app.model.Training;
import com.gcs.app.storage.InMemoryStorage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class TrainingDaoImpl implements TrainingDao {

    @Setter(onMethod_ = @Autowired)
    private InMemoryStorage storage;

    @Override
    public Training create(Training training) {
        Long id = storage.getIdGenerator().getAndIncrement();
        training.setId(id);
        storage.getTrainingStorage().put(id, training);
        log.info("Created training with id: {}", id);
        return training;
    }

    @Override
    public Optional<Training> get(Long id) {
        Training training = storage.getTrainingStorage().get(id);
        log.debug("Retrieved training with id: {}, found: {}", id, training != null);
        return Optional.ofNullable(training);
    }

    @Override
    public List<Training> getAll() {
        List<Training> trainings = List.copyOf(storage.getTrainingStorage().values());
        log.debug("Retrieved {} trainings", trainings.size());
        return trainings;
    }
}