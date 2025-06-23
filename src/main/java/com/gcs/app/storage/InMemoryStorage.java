package com.gcs.app.storage;

import com.gcs.app.model.Trainee;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.Training;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Component
@RequiredArgsConstructor
@Getter
@Slf4j
public class InMemoryStorage {

    private final Map<Long, Trainee> traineeStorage = new HashMap<>();
    private final Map<Long, Trainer> trainerStorage = new HashMap<>();
    private final Map<Long, Training> trainingStorage = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    private final DataInitializer dataInitializer;

    @Value("${storage.path}")
    private String initFilePath;

    @PostConstruct
    private void init() {
        log.info("Initializing in-memory storage from file: {}", initFilePath);
        try {
            dataInitializer.load(initFilePath, traineeStorage, trainerStorage, trainingStorage, idGenerator);
            log.info("Initialization complete: {} trainees, {} trainers, {} trainings",
                    traineeStorage.size(), trainerStorage.size(), trainingStorage.size());
        } catch (IOException e) {
            log.error("Failed to initialize in-memory storage", e);
            throw new RuntimeException("Initialization failed", e);
        }
    }
}
