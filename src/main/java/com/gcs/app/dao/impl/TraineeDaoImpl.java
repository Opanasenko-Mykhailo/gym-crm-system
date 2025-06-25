package com.gcs.app.dao.impl;

import com.gcs.app.dao.TraineeDao;
import com.gcs.app.model.Trainee;
import com.gcs.app.model.enums.EntityType;
import com.gcs.app.storage.StorageGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class TraineeDaoImpl implements TraineeDao {

    private final StorageGateway storage;

    @Override
    public Trainee create(Trainee trainee) {
        Long id = storage.save(EntityType.TRAINEE, trainee);
        trainee.setUserId(id);
        log.info("Created trainee with ID: {}", id);

        return trainee;
    }

    @Override
    public Optional<Trainee> get(Long userId) {
        return storage.find(EntityType.TRAINEE, userId, Trainee.class);
    }

    @Override
    public Trainee update(Trainee trainee) {
        storage.update(EntityType.TRAINEE, trainee.getUserId(), trainee);
        log.info("Updated trainee with ID: {}", trainee.getUserId());

        return trainee;
    }

    @Override
    public void delete(Long userId) {
        storage.delete(EntityType.TRAINEE, userId);
        log.info("Deleted trainee with ID: {}", userId);
    }

    @Override
    public List<Trainee> getAll() {
        List<Trainee> all = storage.findAll(EntityType.TRAINEE, Trainee.class);
        log.debug("Retrieved {} trainees", all.size());

        return all;
    }
}
