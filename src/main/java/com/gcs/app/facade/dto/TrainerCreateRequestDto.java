package com.gcs.app.facade.dto;

import com.gcs.app.model.TrainingType;
import lombok.Data;

@Data
public class TrainerCreateRequestDto {
    private String firstName;
    private String lastName;
    private TrainingType specialization;
}
