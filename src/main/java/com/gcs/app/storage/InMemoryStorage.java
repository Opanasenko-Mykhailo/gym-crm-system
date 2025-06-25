package com.gcs.app.storage;

import com.gcs.app.model.Trainee;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.Training;
import com.gcs.app.model.enums.EntityType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryStorage {

    private final Map<EntityType, Map<Long, ?>> namespaces;
    private final AtomicLong idGenerator = new AtomicLong(1);

    public InMemoryStorage(
            Map<Long, Trainee> traineeStorage,
            Map<Long, Trainer> trainerStorage,
            Map<Long, Training> trainingStorage
    ) {
        namespaces = new HashMap<>();
        namespaces.put(EntityType.TRAINEE, traineeStorage);
        namespaces.put(EntityType.TRAINER, trainerStorage);
        namespaces.put(EntityType.TRAINING, trainingStorage);
    }

    @SuppressWarnings("unchecked")
    public <T> Map<Long, T> getNamespace(EntityType type) {
        return (Map<Long, T>) namespaces.get(type);
    }

    public <T> void put(EntityType type, Long id, T value) {
        getNamespace(type).put(id, value);
    }

    public Long nextId() {
        return idGenerator.getAndIncrement();
    }
}
