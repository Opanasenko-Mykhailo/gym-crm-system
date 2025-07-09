package com.gcs.app.service;

import com.gcs.app.facade.dto.TraineeCreateRequestDto;
import com.gcs.app.facade.dto.TraineeUpdateRequestDto;
import com.gcs.app.facade.dto.PasswordChangeRequestDto;
import com.gcs.app.model.Trainee;

public interface TraineeService {
    Trainee createTrainee(TraineeCreateRequestDto traineeCreateRequestDto);
    Trainee updateTrainee(TraineeUpdateRequestDto traineeUpdateRequestDto);
    void deleteTraineeByUsername(String username);
    Trainee getByUsername(String username);
    void changePassword(PasswordChangeRequestDto passwordChangeRequestDto);
}
