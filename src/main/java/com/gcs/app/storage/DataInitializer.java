package com.gcs.app.storage;

import com.gcs.app.model.*;
import com.gcs.app.exception.StorageInitializationException;
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
        try {
            load(initFilePath);
            log.info("Initialization complete: {} trainees, {} trainers, {} trainings",
                    traineeStorage.size(), trainerStorage.size(), trainingStorage.size());
        } catch (IOException e) {
            throw new StorageInitializationException(
                    String.format("Failed to initialize in-memory storage from file: %s", initFilePath), e);
        }
    }

    public void load(String path) throws IOException {
        Resource resource = resourceLoader.getResource(path);
        if (!resource.exists()) {
            throw new StorageInitializationException(String.format("Resource not found: %s", path));
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

            try {
                String[] parts = line.split(",");
                if (parts.length < 2) {
                    throw new StorageInitializationException(
                            String.format("Invalid format at line %d: %s", lineNumber, line));
                }

                processEntity(parts, lineNumber);
            } catch (Exception e) {
                throw new StorageInitializationException(
                        String.format("Error processing line %d: %s", lineNumber, line), e);
            }
        }
    }

    private void processEntity(String[] parts, int lineNumber) {
        String entityType = parts[0].trim().toLowerCase();

        try {
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
                    throw new StorageInitializationException(
                            String.format("Unknown entity type at line %d: %s", lineNumber, entityType));
            }
        } catch (Exception e) {
            throw new StorageInitializationException(
                    String.format("Failed to process %s entity at line %d", entityType, lineNumber), e);
        }
    }

    private void processTrainee(String[] parts, int lineNumber) {
        if (parts.length != 5) {
            throw new StorageInitializationException(
                    String.format("Invalid trainee format at line %d: %s", lineNumber, String.join(",", parts)));
        }

        try {
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
        } catch (Exception e) {
            throw new StorageInitializationException(
                    String.format("Failed to process trainee at line %d", lineNumber), e);
        }
    }

    private void processTrainer(String[] parts, int lineNumber) {
        if (parts.length != 4) {
            throw new StorageInitializationException(
                    String.format("Invalid trainer format at line %d: %s", lineNumber, String.join(",", parts)));
        }

        try {
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
        } catch (Exception e) {
            throw new StorageInitializationException(
                    String.format("Failed to process trainer at line %d", lineNumber), e);
        }
    }

    private void processTraining(String[] parts, int lineNumber) {
        if (parts.length != 7) {
            throw new StorageInitializationException(
                    String.format("Invalid training format at line %d: %s", lineNumber, String.join(",", parts)));
        }

        try {
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
        } catch (Exception e) {
            throw new StorageInitializationException(
                    String.format("Failed to process training at line %d", lineNumber), e);
        }
    }
}