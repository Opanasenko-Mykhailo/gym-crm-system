package com.gcs.app.dao;

import com.gcs.app.model.Trainer;
import java.util.List;
import java.util.Optional;

public interface TrainerDao {
    Trainer create(Trainer trainer);
    Optional<Trainer> get(Long userId);
    Trainer update(Trainer trainer);
    List<Trainer> getAll();
}
