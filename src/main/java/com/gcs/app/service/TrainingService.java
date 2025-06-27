package com.gcs.app.service;

import com.gcs.app.dto.TrainingCreateRequestDto;
import com.gcs.app.model.Training;

public interface TrainingService {
    Training createTraining(TrainingCreateRequestDto createRequestDto);
    Training getTraining(Long id);
}
