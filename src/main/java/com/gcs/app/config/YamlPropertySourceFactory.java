package com.gcs.app.config;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;

import java.io.IOException;

public class YamlPropertySourceFactory extends DefaultPropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(@NonNull String name, EncodedResource resource)
            throws IOException {
        Resource springResource = resource.getResource();
        String filename = springResource.getFilename();

        if (!filename.endsWith(".yml")) {
            return super.createPropertySource(name, resource);
        }

        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(springResource);
        factory.afterPropertiesSet();

        return new PropertiesPropertySource(filename, factory.getObject());
    }
}