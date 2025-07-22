package com.gcs.app.dao;

import com.gcs.app.model.TrainingType;

import java.util.List;

public interface TrainingTypeDao {
    List<TrainingType> findAll();
}
