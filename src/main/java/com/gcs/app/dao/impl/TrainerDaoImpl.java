package com.gcs.app.dao.impl;

import com.gcs.app.dao.TrainerDao;
import com.gcs.app.dao.UserDao;
import com.gcs.app.exception.EntityNotFoundException;
import com.gcs.app.model.Trainer;
import com.gcs.app.storage.InMemoryStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.gcs.app.model.enums.EntityType.TRAINER;

@Repository
@Slf4j
public class TrainerDaoImpl extends UserDao implements TrainerDao {

    public TrainerDaoImpl(InMemoryStorage storage) {
        super(storage);
    }

    @Override
    public Trainer create(Trainer trainer) {
        Long userId = storage.nextId();
        Trainer trainerWithId = trainer.toBuilder().userId(userId).build();

        storage.put(TRAINER, userId, trainerWithId);
        log.info("Created trainer with userId: {}", userId);

        return trainer;
    }

    @Override
    public Optional<Trainer> get(Long userId) {
        return storage.getById(TRAINER, userId);
    }

    @Override
    public Trainer update(Trainer trainer) {
        Long userId = trainer.getUserId();
        if (storage.getById(TRAINER, userId).isEmpty()) {
            throw new EntityNotFoundException(String.format("Trainer with userId: %d not found", userId));
        }

        storage.put(TRAINER, userId, trainer);
        log.info("Updated trainer with userId: {}", userId);

        return trainer;
    }
}