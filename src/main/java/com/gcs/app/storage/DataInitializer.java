package com.gcs.app.storage;

import com.gcs.app.model.*;
import com.gcs.app.util.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

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

    public void load(String path,
                     Map<Long, Trainee> traineeStorage,
                     Map<Long, Trainer> trainerStorage,
                     Map<Long, Training> trainingStorage,
                     AtomicLong idGenerator) throws IOException {

        Resource resource = resourceLoader.getResource(path);
        if (!resource.exists()) throw new IOException("Resource not found: " + path);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 2) {
                    log.warn("Invalid format at line {}: {}", lineNumber, line);
                    continue;
                }

                try {
                    switch (parts[0].trim().toLowerCase()) {
                        case "trainee" -> processTrainee(parts, lineNumber, traineeStorage, idGenerator);
                        case "trainer" -> processTrainer(parts, lineNumber, trainerStorage, idGenerator);
                        case "training" -> processTraining(parts, lineNumber, trainingStorage, idGenerator);
                        default -> log.warn("Unknown entity type at line {}: {}", lineNumber, parts[0]);
                    }
                } catch (Exception e) {
                    log.error("Error processing line {}: {}", lineNumber, line, e);
                }
            }
        }
    }

    private void processTrainee(String[] parts, int lineNumber, Map<Long, Trainee> storage, AtomicLong idGen) {
        if (parts.length != 5) {
            log.warn("Invalid trainee format at line {}: {}", lineNumber, String.join(",", parts));
            return;
        }

        Trainee trainee = Trainee.builder()
                .userId(idGen.getAndIncrement())
                .firstName(parts[1].trim())
                .lastName(parts[2].trim())
                .username(UserUtils.generateUsername(parts[1].trim(), parts[2].trim(), storage))
                .password(UserUtils.generateRandomPassword())
                .isActive(true)
                .dateOfBirth(LocalDate.parse(parts[3].trim()))
                .address(parts[4].trim())
                .build();

        storage.put(trainee.getUserId(), trainee);
    }

    private void processTrainer(String[] parts, int lineNumber, Map<Long, Trainer> storage, AtomicLong idGen) {
        if (parts.length != 4) {
            log.warn("Invalid trainer format at line {}: {}", lineNumber, String.join(",", parts));
            return;
        }

        Trainer trainer = Trainer.builder()
                .userId(idGen.getAndIncrement())
                .firstName(parts[1].trim())
                .lastName(parts[2].trim())
                .username(UserUtils.generateUsername(parts[1].trim(), parts[2].trim(), storage))
                .password(UserUtils.generateRandomPassword())
                .isActive(true)
                .specialization(new TrainingType(parts[3].trim()))
                .build();

        storage.put(trainer.getUserId(), trainer);
    }

    private void processTraining(String[] parts, int lineNumber, Map<Long, Training> storage, AtomicLong idGen) {
        if (parts.length != 7) {
            log.warn("Invalid training format at line {}: {}", lineNumber, String.join(",", parts));
            return;
        }

        Training training = Training.builder()
                .id(idGen.getAndIncrement())
                .traineeId(Long.parseLong(parts[1].trim()))
                .trainerId(Long.parseLong(parts[2].trim()))
                .name(parts[3].trim())
                .type(new TrainingType(parts[4].trim()))
                .date(LocalDate.parse(parts[5].trim()))
                .duration(Duration.parse(parts[6].trim()))
                .build();

        storage.put(training.getId(), training);
    }
}
