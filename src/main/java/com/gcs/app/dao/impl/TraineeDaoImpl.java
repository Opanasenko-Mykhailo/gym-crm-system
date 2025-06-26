package com.gcs.app.dao.impl;

import com.gcs.app.dao.TraineeDao;
import com.gcs.app.exception.EntityNotFoundException;
import com.gcs.app.model.Trainee;
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
public class TraineeDaoImpl implements TraineeDao {

    private final InMemoryStorage storage;

    @Override
    public Trainee create(Trainee trainee) {
        Long userId = storage.nextId();
        trainee.setUserId(userId);

        storage.put(EntityType.TRAINEE, userId, trainee);
        log.info("Created trainee with userId: {}", userId);

        return trainee;
    }

    @Override
    public Optional<Trainee> get(Long userId) {
        return storage.getById(EntityType.TRAINEE, userId);
    }

    @Override
    public Trainee update(Trainee trainee) {
        Long userId = trainee.getUserId();

        if (storage.getById(EntityType.TRAINEE, userId).isEmpty()) {
            throw new EntityNotFoundException(String.format("Trainee with userId: %d not found", userId));
        }

        storage.put(EntityType.TRAINEE, userId, trainee);
        log.info("Updated trainee with userId: {}", userId);

        return trainee;
    }

    @Override
    public void delete(Long userId) {
        if (storage.getById(EntityType.TRAINEE, userId).isEmpty()) {
            throw new EntityNotFoundException(String.format("Trainee with userId: %d not found", userId));
        }

        storage.getNamespace(EntityType.TRAINEE).remove(userId);
        log.info("Deleted trainee with userId: {}", userId);
    }

    @Override
    public List<Trainee> getAll() {
        List<Trainee> trainees = storage.getAll(EntityType.TRAINEE);
        log.debug("Retrieved {} trainees", trainees.size());

        return trainees;
    }
}