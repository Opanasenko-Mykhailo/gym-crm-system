package com.gcs.app.dao;

import com.gcs.app.model.Trainee;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.enums.EntityType;
import com.gcs.app.storage.InMemoryStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.gcs.app.model.enums.EntityType.TRAINEE;
import static com.gcs.app.model.enums.EntityType.TRAINER;

@Component
@RequiredArgsConstructor
public class UserDao {

    protected final InMemoryStorage storage;

    public Set<String> getAllUsernames() {
        return Stream.concat(
                getUsernamesFromNamespace(TRAINEE, trainee -> ((Trainee) trainee).getUsername()),
                getUsernamesFromNamespace(TRAINER, trainer -> ((Trainer) trainer).getUsername())
        ).collect(Collectors.toSet());
    }

    private Stream<String> getUsernamesFromNamespace(EntityType type, Function<Object, String> usernameMapper) {
        return storage.getNamespace(type).values().stream().map(usernameMapper);
    }
}