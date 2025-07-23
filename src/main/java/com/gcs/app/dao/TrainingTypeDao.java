package com.gcs.app.dao;

import com.gcs.app.model.TrainingType;

import java.util.List;
import java.util.Optional;

public interface TrainingTypeDao {
    List<TrainingType> findAll();
    Optional<TrainingType> findByName(String name);
}
