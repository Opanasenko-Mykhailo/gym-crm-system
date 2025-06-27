package com.gcs.app.dto;

import com.gcs.app.model.TrainingType;
import lombok.Data;

@Data
public class TrainerUpdateRequestDto {
    private Long userId;
    private String firstName;
    private String lastName;
    private String password;
    private Boolean isActive;
    private TrainingType specialization;
}
