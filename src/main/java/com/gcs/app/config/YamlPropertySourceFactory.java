package com.gcs.app.config;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.Properties;

public class YamlPropertySourceFactory extends DefaultPropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(@NonNull String name, EncodedResource resource)
            throws IOException {

        Resource springResource = resource.getResource();
        springResource.getFilename();
        if (springResource.getFilename().endsWith(".yml")) {
            YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
            factory.setResources(springResource);
            factory.afterPropertiesSet();
            Properties properties = factory.getObject();
            String sourceName = springResource.getFilename();
            return new PropertiesPropertySource(sourceName, properties);
        }
        return super.createPropertySource(name, resource);
    }
}