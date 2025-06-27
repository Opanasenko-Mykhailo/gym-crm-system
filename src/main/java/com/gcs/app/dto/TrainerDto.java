package com.gcs.app.dto;

import com.gcs.app.model.TrainingType;
import lombok.Data;

@Data
public class TrainerDto {
    private Long userId;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private Boolean isActive;
    private TrainingType specialization;
}
