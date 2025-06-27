package com.gcs.app.dao;

import com.gcs.app.model.Trainee;
import com.gcs.app.model.Trainer;
import com.gcs.app.storage.InMemoryStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
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
                storage.getNamespace(TRAINEE).values().stream().map(trainee -> ((Trainee) trainee).getUsername()),
                storage.getNamespace(TRAINER).values().stream().map(trainer -> ((Trainer) trainer).getUsername())
        ).collect(Collectors.toSet());
    }
}