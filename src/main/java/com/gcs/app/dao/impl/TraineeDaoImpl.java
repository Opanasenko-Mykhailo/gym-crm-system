package com.gcs.app.dao.impl;

import com.gcs.app.dao.TraineeDao;
import com.gcs.app.model.Trainee;
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
public class TraineeDaoImpl implements TraineeDao {

    private final InMemoryStorage storage;

    @Override
    public Trainee create(Trainee trainee) {
        Long userId = storage.getNextId();
        trainee.setUserId(userId);

        storage.getNamespace(EntityType.TRAINEE).put(userId, trainee);
        log.info("Created trainee with userId: {}", userId);

        return trainee;
    }

    @Override
    public Optional<Trainee> get(Long userId) {
        Trainee trainee = (Trainee) storage.getNamespace(EntityType.TRAINEE).get(userId);
        return Optional.ofNullable(trainee);
    }

    @Override
    public Trainee update(Trainee trainee) {
        Long userId = trainee.getUserId();

        if (!storage.getNamespace(EntityType.TRAINEE).containsKey(userId)) {
            throw new EntityNotFoundException("Trainee with ID " + userId + " not found.");
        }

        storage.getNamespace(EntityType.TRAINEE).put(userId, trainee);
        log.info("Updated trainee with userId: {}", userId);

        return trainee;
    }

    @Override
    public void delete(Long userId) {
        Trainee removedTrainee = (Trainee) storage.getNamespace(EntityType.TRAINEE).remove(userId);

        if (removedTrainee != null) {
            log.info("Deleted trainee with userId: {}", userId);
            return;
        }

        throw new EntityNotFoundException("Trainee with ID " + userId + " not found.");
    }

    @Override
    public List<Trainee> getAll() {
        List<Trainee> trainees = storage.getNamespace(EntityType.TRAINEE).values()
                .stream()
                .map(Trainee.class::cast)
                .collect(Collectors.toList());
        log.debug("Retrieved {} trainees", trainees.size());

        return trainees;
    }
}