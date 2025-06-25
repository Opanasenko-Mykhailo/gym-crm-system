package com.gcs.app.storage;

import com.gcs.app.exception.StorageInitializationException;
import com.gcs.app.model.Trainee;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.Training;
import com.gcs.app.model.enums.EntityType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class StorageInitializerPostProcessor implements BeanPostProcessor {

    private final InMemoryStorage inMemoryStorage;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DataInitializer initializer) {
            try {
                log.info("Running post-processor for DataInitializer to initialize InMemoryStorage");

                Map<EntityType, List<Object>> initializedData = initializer.initializeData();
                initializedData.forEach(this::processEntityList);

                log.info("InMemoryStorage population complete.");
            } catch (Exception e) {
                throw new StorageInitializationException("Failed to populate InMemoryStorage", e);
            }
        }

        return bean;
    }

    private void processEntityList(EntityType entityType, List<Object> entities) {
        for (Object entity : entities) {
            long id = inMemoryStorage.nextId();

            switch (entityType) {
                case TRAINEE -> {
                    Trainee trainee = (Trainee) entity;
                    trainee.setUserId(id);
                    inMemoryStorage.put(entityType, id, trainee);
                }
                case TRAINER -> {
                    Trainer trainer = (Trainer) entity;
                    trainer.setUserId(id);
                    inMemoryStorage.put(entityType, id, trainer);
                }
                case TRAINING -> {
                    Training training = (Training) entity;
                    training.setId(id);
                    inMemoryStorage.put(entityType, id, training);
                }
                default -> log.warn("Unknown EntityType: {}", entityType);
            }
        }
    }
}
