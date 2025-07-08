package com.gcs.app.service;

import com.gcs.app.facade.dto.TraineeCreateRequestDto;
import com.gcs.app.facade.dto.TraineeUpdateRequestDto;
import com.gcs.app.model.Trainee;

public interface TraineeService {
    Trainee createTrainee(TraineeCreateRequestDto traineeCreateRequestDto);
    Trainee updateTrainee(TraineeUpdateRequestDto traineeUpdateRequestDto);
    void deleteTrainee(Long userId);
    void deleteTraineeByUsername(String username);
    Trainee getTrainee(Long userId);
    Trainee getByUsername(String username);
    boolean authenticateTrainee(String username, String password);
}
