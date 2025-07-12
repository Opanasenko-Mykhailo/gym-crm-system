package com.gcs.app.service;

import com.gcs.app.facade.dto.TrainerCreateRequestDto;
import com.gcs.app.facade.dto.TrainerTrainingSearchCriteriaDto;
import com.gcs.app.facade.dto.TrainerUpdateRequestDto;
import com.gcs.app.model.Trainee;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.Training;

import java.util.List;

public interface TrainerService {
    Trainer createTrainer(TrainerCreateRequestDto trainerCreateRequestDto);
    Trainer updateTrainer(TrainerUpdateRequestDto trainerUpdateRequestDto);
    Trainer getByUsername(String username);
    List<Training> getTrainerTrainings(TrainerTrainingSearchCriteriaDto criteria);
    void setTrainerActivationStatus(String username, boolean isActive);
    List<Trainer> getUnassignedForTrainee(Trainee trainee);
}
