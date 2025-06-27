package com.gcs.app.service;

import com.gcs.app.dto.TraineeCreateRequestDto;
import com.gcs.app.dto.TraineeUpdateRequestDto;
import com.gcs.app.model.Trainee;

public interface TraineeService {
    Trainee createTrainee(TraineeCreateRequestDto traineeCreateRequestDto);
    Trainee updateTrainee(TraineeUpdateRequestDto traineeUpdateRequestDto);
    void deleteTrainee(Long userId);
    Trainee getTrainee(Long userId);
}
