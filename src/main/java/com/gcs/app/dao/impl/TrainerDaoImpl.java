package com.gcs.app.dao.impl;

import com.gcs.app.dao.TrainerDao;
import com.gcs.app.model.Trainer;
import com.gcs.app.exception.EntityNotFoundException;
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
public class TrainerDaoImpl implements TrainerDao {

    @Setter(onMethod_ = @Autowired)
    private Map<Long, Trainer> trainerStorage;

    @Setter(onMethod_ = @Autowired)
    private AtomicLong idGenerator;

    @Override
    public Trainer create(Trainer trainer) {
        Long userId = idGenerator.getAndIncrement();
        trainer.setUserId(userId);

        trainerStorage.put(userId, trainer);
        log.info("Created trainer with userId: {}", userId);

        return trainer;
    }

    @Override
    public Optional<Trainer> get(Long userId) {
        Trainer trainer = trainerStorage.get(userId);

        return Optional.ofNullable(trainer);
    }

    @Override
    public Trainer update(Trainer trainer) {
        Long userId = trainer.getUserId();

        if (!trainerStorage.containsKey(userId)) {
            throw new EntityNotFoundException("Trainer" + " with ID " + userId + " not found.");
        }

        trainerStorage.put(userId, trainer);
        log.info("Updated trainer with userId: {}", userId);

        return trainer;
    }

    @Override
    public List<Trainer> getAll() {
        List<Trainer> trainers = List.copyOf(trainerStorage.values());
        log.debug("Retrieved {} trainers", trainers.size());

        return trainers;
    }
}