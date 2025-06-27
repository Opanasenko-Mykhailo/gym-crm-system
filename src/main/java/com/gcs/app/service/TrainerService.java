package com.gcs.app.service;

import com.gcs.app.model.Trainer;

public interface TrainerService {
    Trainer createTrainer(Trainer trainer);
    Trainer updateTrainer(Trainer updatedTrainer);
    Trainer getTrainer(Long userId);
}
