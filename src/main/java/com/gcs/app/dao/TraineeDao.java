package com.gcs.app.dao;

import com.gcs.app.model.Trainee;

import java.util.Optional;

public interface TraineeDao {
    Trainee create(Trainee trainee);
    Trainee update(Trainee trainee);
    void deleteByUsername(String username);
    Optional<Trainee> findByUsername(String username);
}
