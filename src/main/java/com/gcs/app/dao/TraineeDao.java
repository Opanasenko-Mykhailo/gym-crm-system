package com.gcs.app.dao;

import com.gcs.app.model.Trainee;
import java.util.Optional;
import java.util.Set;

public interface TraineeDao {
    Trainee create(Trainee trainee);
    Optional<Trainee> get(Long userId);
    Trainee update(Trainee trainee);
    void delete(Long userId);
    Optional<Trainee> findByUsername(String username);
}
