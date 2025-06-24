package com.gcs.app.dao.impl;

import com.gcs.app.dao.TraineeDao;
import com.gcs.app.model.Trainee;
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
public class TraineeDaoImpl implements TraineeDao {

    @Setter(onMethod_ = @Autowired)
    private Map<Long, Trainee> traineeStorage;

    @Setter(onMethod_ = @Autowired)
    private AtomicLong idGenerator;

    @Override
    public Trainee create(Trainee trainee) {
        Long userId = idGenerator.getAndIncrement();
        trainee.setUserId(userId);

        traineeStorage.put(userId, trainee);
        log.info("Created trainee with userId: {}", userId);

        return trainee;
    }

    @Override
    public Optional<Trainee> get(Long userId) {
        Trainee trainee = traineeStorage.get(userId);
        log.debug("Retrieved trainee with userId: {}, found: {}", userId, trainee != null);

        return Optional.ofNullable(trainee);
    }

    @Override
    public Trainee update(Trainee trainee) {
        Long userId = trainee.getUserId();

        if (!traineeStorage.containsKey(userId)) {
            throw new IllegalArgumentException("Trainee with userId " + userId + " not found");
        }

        traineeStorage.put(userId, trainee);
        log.info("Updated trainee with userId: {}", userId);

        return trainee;
    }

    @Override
    public void delete(Long userId) {
        Trainee removedTrainee = traineeStorage.remove(userId);

        if (removedTrainee != null) {
            log.info("Deleted trainee with userId: {}", userId);
            return;
        }

        throw new IllegalArgumentException("Trainee with userId " + userId + " not found");
    }

    @Override
    public List<Trainee> getAll() {
        List<Trainee> trainees = List.copyOf(traineeStorage.values());
        log.debug("Retrieved {} trainees", trainees.size());

        return trainees;
    }
}