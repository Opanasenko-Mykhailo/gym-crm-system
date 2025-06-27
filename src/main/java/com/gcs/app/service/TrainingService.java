package com.gcs.app.service;

import com.gcs.app.model.Training;

public interface TrainingService {
    Training createTraining(Training training);
    Training getTraining(Long id);
}
