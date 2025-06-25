package com.gcs.app.storage;

import com.gcs.app.exception.EntityNotFoundException;
import com.gcs.app.model.enums.EntityType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InMemoryStorageGateway implements StorageGateway {

    private final InMemoryStorage storage;

    @Override
    public <T> Long save(EntityType type, T entity) {
        Long id = storage.nextId();
        storage.put(type, id, entity);

        return id;
    }

    @Override
    public <T> Optional<T> find(EntityType type, Long id, Class<T> clazz) {
        Map<Long, ?> namespace = getNamespace(type);

        return Optional.ofNullable(clazz.cast(namespace.get(id)));
    }

    @Override
    public <T> List<T> findAll(EntityType type, Class<T> clazz) {
        Map<Long, ?> namespace = getNamespace(type);

        return namespace.values().stream()
                .map(clazz::cast)
                .collect(Collectors.toList());
    }

    @Override
    public <T> void update(EntityType type, Long id, T entity) {
        Map<Long, T> namespace = getNamespace(type);

        if (!namespace.containsKey(id)) {
            throw new EntityNotFoundException(String.format("Entity not found with id: %d", id));
        }

        namespace.put(id, entity);
    }

    @Override
    public <T> void delete(EntityType type, Long id) {
        Map<Long, ?> namespace = getNamespace(type);

        if (namespace.remove(id) == null) {
            throw new EntityNotFoundException(String.format("Entity not found with id: %d", id));
        }
    }

    @Override
    public <T> Map<Long, T> getNamespace(EntityType type) {
        return storage.getNamespace(type);
    }
}
