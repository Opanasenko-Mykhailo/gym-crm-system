package com.gcs.app.dao.impl;

import com.gcs.app.dao.TrainerDao;
import com.gcs.app.exception.EntityNotFoundException;
import com.gcs.app.model.Trainer;
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
public class TrainerDaoImpl implements TrainerDao {

    private final InMemoryStorage storage;

    @Override
    public Trainer create(Trainer trainer) {
        Long userId = storage.nextId();
        trainer.setUserId(userId);

        storage.put(EntityType.TRAINER, userId, trainer);
        log.info("Created trainer with userId: {}", userId);

        return trainer;
    }

    @Override
    public Optional<Trainer> get(Long userId) {
        Optional<Trainer> trainer = storage.getById(EntityType.TRAINER, userId);
        log.debug("Retrieved trainer with userId: {}, found: {}", userId, trainer.isPresent());

        return trainer;
    }

    @Override
    public Trainer update(Trainer trainer) {
        Long userId = trainer.getUserId();

        if (storage.getById(EntityType.TRAINER, userId).isPresent()) {
            storage.put(EntityType.TRAINER, userId, trainer);
            log.info("Updated trainer with userId: {}", userId);

            return trainer;
        }
        throw new EntityNotFoundException(String.format("Trainer with userId: %s not found", userId));
    }

    @Override
    public List<Trainer> getAll() {
        List<Trainer> trainers = storage.getAll(EntityType.TRAINER);
        log.debug("Retrieved {} trainers", trainers.size());

        return trainers;
    }
}