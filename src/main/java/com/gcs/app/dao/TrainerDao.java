package com.gcs.app.dao;

import com.gcs.app.model.Trainer;
import java.util.Optional;

public interface TrainerDao {
    Trainer create(Trainer trainer);
    Trainer update(Trainer trainer);
    Optional<Trainer> findByUsername(String username);
}
