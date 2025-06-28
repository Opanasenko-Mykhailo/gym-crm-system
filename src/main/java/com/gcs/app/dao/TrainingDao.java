package com.gcs.app.dao;

import com.gcs.app.model.Training;
import java.util.Optional;

public interface TrainingDao {
    Training create(Training training);
    Optional<Training> get(Long id);
}