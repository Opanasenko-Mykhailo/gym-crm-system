package com.gcs.app.dao;

import com.gcs.app.facade.dto.TrainerTrainingSearchCriteriaDto;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.Training;

import java.util.List;
import java.util.Optional;

public interface TrainerDao {
    Trainer create(Trainer trainer);
    Trainer update(Trainer trainer);
    Optional<Trainer> findByUsername(String username);
    List<Training> findByTrainerAndCriteria(TrainerTrainingSearchCriteriaDto criteria);
}
