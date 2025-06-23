package com.gcs.app.dao.impl;

import com.gcs.app.dao.TraineeDao;
import com.gcs.app.model.Trainee;
import com.gcs.app.storage.InMemoryStorage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class TraineeDaoImpl implements TraineeDao {

    @Setter(onMethod_ = @Autowired)
    private InMemoryStorage storage;

    @Override
    public Trainee create(Trainee trainee) {
        Long userId = storage.getIdGenerator().getAndIncrement();
        trainee.setUserId(userId);
        storage.getTraineeStorage().put(userId, trainee);
        log.info("Created trainee with userId: {}", userId);
        return trainee;
    }

    @Override
    public Optional<Trainee> get(Long userId) {
        Trainee trainee = storage.getTraineeStorage().get(userId);
        log.debug("Retrieved trainee with userId: {}, found: {}", userId, trainee != null);
        return Optional.ofNullable(trainee);
    }

    @Override
    public Trainee update(Trainee trainee) {
        Long userId = trainee.getUserId();
        if (storage.getTraineeStorage().containsKey(userId)) {
            storage.getTraineeStorage().put(userId, trainee);
            log.info("Updated trainee with userId: {}", userId);
            return trainee;
        }
        log.warn("Trainee with userId: {} not found", userId);
        throw new IllegalArgumentException("Trainee not found");
    }

    @Override
    public void delete(Long userId) {
        if (storage.getTraineeStorage().remove(userId) != null) {
            log.info("Deleted trainee with userId: {}", userId);
        } else {
            log.warn("Trainee with userId: {} not found", userId);
        }
    }

    @Override
    public List<Trainee> getAll() {
        List<Trainee> trainees = List.copyOf(storage.getTraineeStorage().values());
        log.debug("Retrieved {} trainees", trainees.size());
        return trainees;
    }
}