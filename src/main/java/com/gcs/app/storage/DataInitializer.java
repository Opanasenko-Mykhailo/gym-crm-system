package com.gcs.app.storage;

import com.gcs.app.exception.StorageInitializationException;
import com.gcs.app.model.Trainee;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.Training;
import com.gcs.app.model.enums.EntityType;
import com.gcs.app.model.TrainingType;
import com.gcs.app.util.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final ResourceLoader resourceLoader;
    private final StorageGateway storage;

    @Value("${storage.path}")
    private String initFilePath;

    public void initializeData() {
        try {
            load(initFilePath);
            log.info("Initialization complete");
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
                    processTrainee(parts);
                    break;
                case "trainer":
                    processTrainer(parts);
                    break;
                case "training":
                    processTraining(parts);
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

    private void processTrainee(String[] parts) {
        Map<Long, Trainee> traineeStorage = storage.getNamespace(EntityType.TRAINEE);

        String username = UserUtils.generateUsername(parts[1].trim(), parts[2].trim(), traineeStorage);

        Trainee trainee = Trainee.builder()
                .firstName(parts[1].trim())
                .lastName(parts[2].trim())
                .username(username)
                .password(UserUtils.generateRandomPassword())
                .isActive(true)
                .dateOfBirth(LocalDate.parse(parts[3].trim()))
                .address(parts[4].trim())
                .build();

        storage.save(EntityType.TRAINEE, trainee);
    }

    private void processTrainer(String[] parts) {
        Map<Long, Trainer> trainerStorage = storage.getNamespace(EntityType.TRAINER);

        String username = UserUtils.generateUsername(parts[1].trim(), parts[2].trim(), trainerStorage);

        Trainer trainer = Trainer.builder()
                .firstName(parts[1].trim())
                .lastName(parts[2].trim())
                .username(username)
                .password(UserUtils.generateRandomPassword())
                .isActive(true)
                .specialization(new TrainingType(parts[3].trim()))
                .build();

        storage.save(EntityType.TRAINER, trainer);
    }

    private void processTraining(String[] parts) {
        Training training = Training.builder()
                .traineeId(Long.parseLong(parts[1].trim()))
                .trainerId(Long.parseLong(parts[2].trim()))
                .name(parts[3].trim())
                .type(new TrainingType(parts[4].trim()))
                .date(LocalDate.parse(parts[5].trim()))
                .duration(Duration.parse(parts[6].trim()))
                .build();

        storage.save(EntityType.TRAINING, training);
    }
}
