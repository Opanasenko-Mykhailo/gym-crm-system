package com.gcs.app.dao;

import com.gcs.app.model.Trainee;
import java.util.List;
import java.util.Optional;

public interface TraineeDao {
    Trainee create(Trainee trainee);
    Optional<Trainee> get(Long userId);
    Trainee update(Trainee trainee);
    void delete(Long userId);
    List<Trainee> getAll();
}
