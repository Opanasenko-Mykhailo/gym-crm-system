package com.gcs.app.storage;

import com.gcs.app.model.*;
import com.gcs.app.util.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final ResourceLoader resourceLoader;

    private final Map<Long, Trainee> traineeStorage;
    private final Map<Long, Trainer> trainerStorage;
    private final Map<Long, Training> trainingStorage;
    private final AtomicLong idGenerator;

    @Value("${storage.path}")
    private String initFilePath;

    @PostConstruct
    public void initializeData() {
        log.info("Initializing in-memory storage from file: {}", initFilePath);

        try {
            load(initFilePath);
            log.info("Initialization complete: {} trainees, {} trainers, {} trainings",
                    traineeStorage.size(), trainerStorage.size(), trainingStorage.size());
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize in-memory storage from file: " + initFilePath, e);
        }
    }

    public void load(String path) throws IOException {
        Resource resource = resourceLoader.getResource(path);

        if (!resource.exists()) {
            throw new IOException("Resource not found: " + path);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            processFileLines(reader);
        }
    }

    private void processFileLines(BufferedReader reader) throws IOException {
        String line;
        int lineNumber = 0;

        while ((line = reader.readLine()) != null) {
            lineNumber++;

            if (line.trim().isEmpty()) {
                continue;
            }

            String[] parts = line.split(",");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid format at line " + lineNumber + ": " + line);
            }

            processEntity(parts, lineNumber);
        }
    }

    private void processEntity(String[] parts, int lineNumber) {
        String entityType = parts[0].trim().toLowerCase();

        switch (entityType) {
            case "trainee":
                processTrainee(parts, lineNumber);
                break;
            case "trainer":
                processTrainer(parts, lineNumber);
                break;
            case "training":
                processTraining(parts, lineNumber);
                break;
            default:
                throw new IllegalArgumentException("Unknown entity type at line " + lineNumber + ": " + entityType);
        }
    }

    private void processTrainee(String[] parts, int lineNumber) {
        if (parts.length != 5) {
            throw new IllegalArgumentException("Invalid trainee format at line " + lineNumber + ": " + String.join(",", parts));
        }

        Trainee trainee = Trainee.builder()
                .userId(idGenerator.getAndIncrement())
                .firstName(parts[1].trim())
                .lastName(parts[2].trim())
                .username(UserUtils.generateUsername(parts[1].trim(), parts[2].trim(), traineeStorage))
                .password(UserUtils.generateRandomPassword())
                .isActive(true)
                .dateOfBirth(LocalDate.parse(parts[3].trim()))
                .address(parts[4].trim())
                .build();

        traineeStorage.put(trainee.getUserId(), trainee);
    }

    private void processTrainer(String[] parts, int lineNumber) {
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid trainer format at line " + lineNumber + ": " + String.join(",", parts));
        }

        Trainer trainer = Trainer.builder()
                .userId(idGenerator.getAndIncrement())
                .firstName(parts[1].trim())
                .lastName(parts[2].trim())
                .username(UserUtils.generateUsername(parts[1].trim(), parts[2].trim(), trainerStorage))
                .password(UserUtils.generateRandomPassword())
                .isActive(true)
                .specialization(new TrainingType(parts[3].trim()))
                .build();

        trainerStorage.put(trainer.getUserId(), trainer);
    }

    private void processTraining(String[] parts, int lineNumber) {
        if (parts.length != 7) {
            throw new IllegalArgumentException("Invalid training format at line " + lineNumber + ": " + String.join(",", parts));
        }

        Training training = Training.builder()
                .id(idGenerator.getAndIncrement())
                .traineeId(Long.parseLong(parts[1].trim()))
                .trainerId(Long.parseLong(parts[2].trim()))
                .name(parts[3].trim())
                .type(new TrainingType(parts[4].trim()))
                .date(LocalDate.parse(parts[5].trim()))
                .duration(Duration.parse(parts[6].trim()))
                .build();

        trainingStorage.put(training.getId(), training);
    }
}