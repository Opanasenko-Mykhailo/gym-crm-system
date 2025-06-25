package com.gcs.app.storage;

import com.gcs.app.model.enums.EntityType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryStorage {

    private final Map<EntityType, Map<Long, ?>> namespaces = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @SuppressWarnings("unchecked")
    public <T> Map<Long, T> getNamespace(EntityType type) {
        return (Map<Long, T>) namespaces.computeIfAbsent(type, k -> new ConcurrentHashMap<>());
    }

    public Long getNextId() {
        return idGenerator.getAndIncrement();
    }
}