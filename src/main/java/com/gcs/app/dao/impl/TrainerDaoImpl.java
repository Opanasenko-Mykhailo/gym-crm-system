package com.gcs.app.dao.impl;

import com.gcs.app.dao.TrainerDao;
import com.gcs.app.model.Trainer;
import com.gcs.app.storage.InMemoryStorage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class TrainerDaoImpl implements TrainerDao {

    @Setter(onMethod_ = @Autowired)
    private InMemoryStorage storage;

    @Override
    public Trainer create(Trainer trainer) {
        Long userId = storage.getIdGenerator().getAndIncrement();
        trainer.setUserId(userId);
        storage.getTrainerStorage().put(userId, trainer);
        log.info("Created trainer with userId: {}", userId);
        return trainer;
    }

    @Override
    public Optional<Trainer> get(Long userId) {
        Trainer trainer = storage.getTrainerStorage().get(userId);
        log.debug("Retrieved trainer with userId: {}, found: {}", userId, trainer != null);
        return Optional.ofNullable(trainer);
    }

    @Override
    public Trainer update(Trainer trainer) {
        Long userId = trainer.getUserId();
        if (storage.getTrainerStorage().containsKey(userId)) {
            storage.getTrainerStorage().put(userId, trainer);
            log.info("Updated trainer with userId: {}", userId);
            return trainer;
        }
        log.warn("Trainer with userId: {} not found", userId);
        throw new IllegalArgumentException("Trainer not found");
    }

    @Override
    public List<Trainer> getAll() {
        List<Trainer> trainers = List.copyOf(storage.getTrainerStorage().values());
        log.debug("Retrieved {} trainers", trainers.size());
        return trainers;
    }
}