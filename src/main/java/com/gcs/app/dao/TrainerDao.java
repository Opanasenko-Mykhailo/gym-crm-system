package com.gcs.app.dao;

import com.gcs.app.model.Trainer;
import java.util.Optional;
import java.util.Set;

public interface TrainerDao {
    Trainer create(Trainer trainer);
    Optional<Trainer> get(Long userId);
    Trainer update(Trainer trainer);
    Optional<Trainer> findByUsername(String username);
}
