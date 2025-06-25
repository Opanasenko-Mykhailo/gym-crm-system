package com.gcs.app.storage;

import com.gcs.app.exception.StorageInitializationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StorageInitializerPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DataInitializer initializer) {

            try {
                log.info("Running custom post-processor for DataInitializer");
                initializer.initializeData();
            } catch (Exception e) {
                throw new StorageInitializationException("Failed during post-processing DataInitializer", e);
            }

        }
        return bean;
    }
}
