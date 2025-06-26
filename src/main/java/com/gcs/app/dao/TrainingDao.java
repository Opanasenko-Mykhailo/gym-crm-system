package com.gcs.app.dao;

import com.gcs.app.model.Training;
import java.util.List;
import java.util.Optional;

public interface TrainingDao {
    Training create(Training training);
    Optional<Training> get(Long id);
    List<Training> getAll();
}