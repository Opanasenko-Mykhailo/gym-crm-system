package com.gcs.app.repository;

import com.gcs.app.facade.dto.TraineeTrainingSearchCriteriaDto;
import com.gcs.app.facade.dto.TrainerTrainingSearchCriteriaDto;
import com.gcs.app.model.Training;
import jakarta.validation.Valid;

import java.util.List;

public interface TrainingQueryRepository {
    List<Training> findTrainingsForTrainee(@Valid TraineeTrainingSearchCriteriaDto criteria);
    List<Training> findTrainingsForTrainer(@Valid TrainerTrainingSearchCriteriaDto criteria);
}

