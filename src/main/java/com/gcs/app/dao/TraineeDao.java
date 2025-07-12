package com.gcs.app.dao;

import com.gcs.app.facade.dto.TraineeTrainingSearchCriteriaDto;
import com.gcs.app.model.Trainee;
import com.gcs.app.model.Training;

import java.util.List;
import java.util.Optional;

public interface TraineeDao {
    Trainee create(Trainee trainee);
    Trainee update(Trainee trainee);
    void deleteByUsername(String username);
    Optional<Trainee> findByUsername(String username);
    List<Training> findByTraineeCriteria(TraineeTrainingSearchCriteriaDto criteria);
}
