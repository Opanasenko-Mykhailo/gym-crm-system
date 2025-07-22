package com.gcs.app.service;

import com.gcs.app.facade.dto.TrainingTypeResponseDto;

import java.util.List;

public interface TrainingTypeService {
    List<TrainingTypeResponseDto> getAll();
}
