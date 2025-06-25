package com.gcs.app.dao.impl;

import com.gcs.app.dao.TrainerDao;
import com.gcs.app.model.Trainer;
import com.gcs.app.exception.EntityNotFoundException;
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
public class TrainerDaoImpl implements TrainerDao {

    private final InMemoryStorage storage;

    @Override
    public Trainer create(Trainer trainer) {
        Long userId = storage.getNextId();
        trainer.setUserId(userId);

        storage.getNamespace(EntityType.TRAINER).put(userId, trainer);
        log.info("Created trainer with userId: {}", userId);

        return trainer;
    }

    @Override
    public Optional<Trainer> get(Long userId) {
        Trainer trainer = (Trainer) storage.getNamespace(EntityType.TRAINER).get(userId);
        return Optional.ofNullable(trainer);
    }

    @Override
    public Trainer update(Trainer trainer) {
        Long userId = trainer.getUserId();

        if (!storage.getNamespace(EntityType.TRAINER).containsKey(userId)) {
            throw new EntityNotFoundException("Trainer with ID " + userId + " not found.");
        }

        storage.getNamespace(EntityType.TRAINER).put(userId, trainer);
        log.info("Updated trainer with userId: {}", userId);

        return trainer;
    }

    @Override
    public List<Trainer> getAll() {
        List<Trainer> trainers = storage.getNamespace(EntityType.TRAINER).values()
                .stream()
                .map(Trainer.class::cast)
                .collect(Collectors.toList());
        log.debug("Retrieved {} trainers", trainers.size());

        return trainers;
    }
}