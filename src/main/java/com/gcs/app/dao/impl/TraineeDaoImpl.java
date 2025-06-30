package com.gcs.app.dao.impl;

import com.gcs.app.dao.TraineeDao;
import com.gcs.app.dao.UserDao;
import com.gcs.app.exception.EntityNotFoundException;
import com.gcs.app.model.Trainee;
import com.gcs.app.storage.InMemoryStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.gcs.app.model.enums.EntityType.TRAINEE;

@Repository
@Slf4j
public class TraineeDaoImpl extends UserDao implements TraineeDao {

    public TraineeDaoImpl(InMemoryStorage storage) {
        super(storage);
    }

    @Override
    public Trainee create(Trainee trainee) {
        Long userId = storage.nextId();
        Trainee traineeWithId = trainee.toBuilder().userId(userId).build();

        storage.put(TRAINEE, userId, traineeWithId);
        log.info("Created trainee with userId: {}", userId);

        return trainee;
    }

    @Override
    public Optional<Trainee> get(Long userId) {
        return storage.getById(TRAINEE, userId);
    }

    @Override
    public Trainee update(Trainee trainee) {
        Long userId = trainee.getUserId();

        if (storage.getById(TRAINEE, userId).isEmpty()) {
            throw new EntityNotFoundException(String.format("Trainee with userId: %d not found", userId));
        }

        storage.put(TRAINEE, userId, trainee);
        log.info("Updated trainee with userId: {}", userId);

        return trainee;
    }

    @Override
    public void delete(Long userId) {
        if (storage.getById(TRAINEE, userId).isEmpty()) {
            throw new EntityNotFoundException(String.format("Trainee with userId: %d not found", userId));
        }

        storage.getNamespace(TRAINEE).remove(userId);
        log.info("Deleted trainee with userId: {}", userId);
    }
}