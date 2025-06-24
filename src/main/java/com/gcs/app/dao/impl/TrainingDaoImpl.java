package com.gcs.app.dao.impl;

import com.gcs.app.dao.TrainingDao;
import com.gcs.app.model.Training;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Slf4j
public class TrainingDaoImpl implements TrainingDao {

    @Setter(onMethod_ = @Autowired)
    private Map<Long, Training> trainingStorage;

    @Setter(onMethod_ = @Autowired)
    private AtomicLong idGenerator;

    @Override
    public Training create(Training training) {
        Long id = idGenerator.getAndIncrement();
        training.setId(id);

        trainingStorage.put(id, training);
        log.info("Created training with id: {}", id);

        return training;
    }

    @Override
    public Optional<Training> get(Long id) {
        Training training = trainingStorage.get(id);

        return Optional.ofNullable(training);
    }

    @Override
    public List<Training> getAll() {
        List<Training> trainings = List.copyOf(trainingStorage.values());
        log.debug("Retrieved {} trainings", trainings.size());

        return trainings;
    }
}