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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
