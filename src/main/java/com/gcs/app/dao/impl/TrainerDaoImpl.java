package com.gcs.app.dao.impl;

import com.gcs.app.dao.TrainerDao;
import com.gcs.app.model.Trainer;
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
public class TrainerDaoImpl implements TrainerDao {

    private final StorageGateway storage;

    @Override
    public Trainer create(Trainer trainer) {
        Long id = storage.save(EntityType.TRAINER, trainer);
        trainer.setUserId(id);
        log.info("Created trainer with userId: {}", id);

        return trainer;
    }

    @Override
    public Optional<Trainer> get(Long userId) {
        return storage.find(EntityType.TRAINER, userId, Trainer.class);
    }

    @Override
    public Trainer update(Trainer trainer) {
        Long id = trainer.getUserId();
        storage.update(EntityType.TRAINER, id, trainer);
        log.info("Updated trainer with userId: {}", id);

        return trainer;
    }

    @Override
    public List<Trainer> getAll() {
        List<Trainer> list = storage.findAll(EntityType.TRAINER, Trainer.class);
        log.debug("Retrieved {} trainers", list.size());

        return list;
    }
}
