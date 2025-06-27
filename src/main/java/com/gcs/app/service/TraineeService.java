package com.gcs.app.service;

import com.gcs.app.model.Trainee;

public interface TraineeService {
    Trainee createTrainee(Trainee trainee);
    Trainee updateTrainee(Trainee updatedTrainee);
    void deleteTrainee(Long userId);
    Trainee getTrainee(Long userId);
}
