package com.gcs.app.storage;

import com.gcs.app.exception.StorageInitializationException;
import com.gcs.app.model.Trainee;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.Training;
import com.gcs.app.model.TrainingType;
import com.gcs.app.model.enums.EntityType;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gcs.app.model.enums.EntityType.TRAINEE;
import static com.gcs.app.model.enums.EntityType.TRAINER;
import static com.gcs.app.model.enums.EntityType.TRAINING;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final ResourceLoader resourceLoader;

    @Value("${storage.path}")
    private String initFilePath;

    public Map<EntityType, List<Object>> initializeData() {
        try {
            Map<EntityType, List<Object>> data = loadData(initFilePath);
            log.info("Data initialization complete");
            return data;
        } catch (IOException e) {
            throw new StorageInitializationException("Failed to initialize data from file: " + initFilePath, e);
        }
    }

    private Map<EntityType, List<Object>> loadData(String path) throws IOException {
        Resource resource = resourceLoader.getResource(path);

        if (!resource.exists()) {
            throw new StorageInitializationException(String.format("Resource not found: %s", path));
        }

        Map<EntityType, List<Object>> data = new HashMap<>();
        data.put(TRAINEE, new ArrayList<>());
        data.put(TRAINER, new ArrayList<>());
        data.put(TRAINING, new ArrayList<>());

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            parseFileLines(reader, data);
        }

        return data;
    }

    private void parseFileLines(BufferedReader reader, Map<EntityType, List<Object>> data) throws IOException {
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
                    throw new StorageInitializationException(String.format("Invalid format at line %d: %s", lineNumber, line));
                }
                parseEntity(parts, lineNumber, data);
            } catch (Exception e) {
                throw new StorageInitializationException(String.format("Error processing line %d: %s", lineNumber, line), e);
            }
        }
    }

    private void parseEntity(String[] parts, int lineNumber, Map<EntityType, List<Object>> data) {
        String entityType = parts[0].trim().toLowerCase();

        switch (entityType) {
            case "trainee" -> data.get(TRAINEE).add(createTrainee(parts));
            case "trainer" -> data.get(TRAINER).add(createTrainer(parts));
            case "training" -> data.get(TRAINING).add(createTraining(parts));
            default ->
                    throw new StorageInitializationException(String.format("Unknown entity type at line %d: %s", lineNumber, entityType));
        }
    }

    private Trainee createTrainee(String[] parts) {
        return Trainee.builder()
                .firstName(parts[1].trim())
                .lastName(parts[2].trim())
                .username(parts[3].trim())
                .password(UserUtils.generateRandomPassword())
                .isActive(true)
                .dateOfBirth(LocalDate.parse(parts[4].trim()))
                .address(parts[5].trim())
                .build();
    }

    private Trainer createTrainer(String[] parts) {
        return Trainer.builder()
                .firstName(parts[1].trim())
                .lastName(parts[2].trim())
                .username(parts[3].trim())
                .password(UserUtils.generateRandomPassword())
                .isActive(true)
                .specialization(new TrainingType(parts[4].trim()))
                .build();
    }

    private Training createTraining(String[] parts) {
        return Training.builder()
                .traineeId(Long.parseLong(parts[1].trim()))
                .trainerId(Long.parseLong(parts[2].trim()))
                .name(parts[4].trim())
                .type(new TrainingType(parts[5].trim()))
                .date(LocalDate.parse(parts[6].trim()))
                .duration(Duration.parse(parts[7].trim()))
                .build();
    }
}

