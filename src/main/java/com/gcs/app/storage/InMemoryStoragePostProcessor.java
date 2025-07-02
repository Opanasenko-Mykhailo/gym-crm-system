package com.gcs.app.storage;

import com.gcs.app.exception.StorageInitializationException;
import com.gcs.app.model.Trainee;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.Training;
import com.gcs.app.model.enums.EntityType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryStoragePostProcessor implements BeanPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof InMemoryStorage inMemoryStorage) {

            try {
                log.info("Running post-processor to initialize InMemoryStorage");

                DataInitializer dataInitializer = applicationContext.getBean(DataInitializer.class);
                Map<EntityType, List<Object>> initializedData = dataInitializer.initializeData();

                initializedData.forEach((entityType, entities) ->
                        processEntityList(entityType, entities, inMemoryStorage));

                log.info("InMemoryStorage population complete.");
            } catch (Exception e) {
                throw new StorageInitializationException("Failed to populate InMemoryStorage", e);
            }
        }

        return bean;
    }

    private void processEntityList(EntityType entityType, List<Object> entities, InMemoryStorage inMemoryStorage) {
        for (Object entity : entities) {
            long id = inMemoryStorage.nextId();

            switch (entityType) {
                case TRAINEE -> {
                    Trainee trainee = (Trainee) entity;
                    Trainee traineeWithId = trainee.toBuilder().id(id).build();
                    inMemoryStorage.put(entityType, id, traineeWithId);
                }
                case TRAINER -> {
                    Trainer trainer = (Trainer) entity;
                    Trainer trainerWithId = trainer.toBuilder().id(id).build();
                    inMemoryStorage.put(entityType, id, trainerWithId);
                }
                case TRAINING -> {
                    Training training = (Training) entity;
                    Training trainingWithId = training.toBuilder().id(id).build();
                    inMemoryStorage.put(entityType, id, trainingWithId);
                }
                default -> log.warn("Unknown EntityType: {}", entityType);
            }
        }
    }
}
