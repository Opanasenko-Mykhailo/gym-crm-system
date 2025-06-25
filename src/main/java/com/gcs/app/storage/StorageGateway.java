package com.gcs.app.storage;

import com.gcs.app.model.enums.EntityType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StorageGateway {
    <T> Long save(EntityType type, T entity);
    <T> Optional<T> find(EntityType type, Long id, Class<T> clazz);
    <T> List<T> findAll(EntityType type, Class<T> clazz);
    <T> void update(EntityType type, Long id, T entity);
    <T> void delete(EntityType type, Long id);
    <T> Map<Long, T> getNamespace(EntityType type);
}
