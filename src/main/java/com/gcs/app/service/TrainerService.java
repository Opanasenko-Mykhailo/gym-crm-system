package com.gcs.app.service;

import com.gcs.app.facade.dto.PasswordChangeRequestDto;
import com.gcs.app.facade.dto.TrainerCreateRequestDto;
import com.gcs.app.facade.dto.TrainerUpdateRequestDto;
import com.gcs.app.model.Trainer;

public interface TrainerService {
    Trainer createTrainer(TrainerCreateRequestDto trainerCreateRequestDto);
    Trainer updateTrainer(TrainerUpdateRequestDto trainerUpdateRequestDto);
    Trainer getTrainer(Long userId);
    Trainer getByUsername(String username);
    boolean authenticateTrainer(String username, String password);
    void changePassword(PasswordChangeRequestDto passwordChangeRequestDto);
}
