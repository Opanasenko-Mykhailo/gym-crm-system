package com.gcs.app.service;

import com.gcs.app.facade.dto.TraineeCreateRequestDto;
import com.gcs.app.facade.dto.TraineeTrainingSearchCriteriaDto;
import com.gcs.app.facade.dto.TraineeUpdateRequestDto;
import com.gcs.app.model.Trainee;
import com.gcs.app.model.Training;

import java.util.List;

public interface TraineeService {
    Trainee createTrainee(TraineeCreateRequestDto traineeCreateRequestDto);
    Trainee updateTrainee(TraineeUpdateRequestDto traineeUpdateRequestDto);
    void deleteTraineeByUsername(String username);
    Trainee getByUsername(String username);
    List<Training> getTraineeTrainings(TraineeTrainingSearchCriteriaDto criteria);
}
