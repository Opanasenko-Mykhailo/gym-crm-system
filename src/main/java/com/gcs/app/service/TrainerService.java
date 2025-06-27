package com.gcs.app.service;

import com.gcs.app.dto.TrainerCreateRequestDto;
import com.gcs.app.dto.TrainerUpdateRequestDto;
import com.gcs.app.model.Trainer;

public interface TrainerService {
    Trainer createTrainer(TrainerCreateRequestDto trainerCreateRequestDto);
    Trainer updateTrainer(TrainerUpdateRequestDto trainerUpdateRequestDto);
    Trainer getTrainer(Long userId);
}
