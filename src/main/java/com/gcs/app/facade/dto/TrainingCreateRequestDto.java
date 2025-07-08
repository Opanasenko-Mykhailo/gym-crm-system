package com.gcs.app.facade.dto;

import com.gcs.app.model.TrainingType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TrainingCreateRequestDto {
    @NotNull(message = "Trainee ID is required")
    private Long traineeId;

    @NotNull(message = "Trainer ID is required")
    private Long trainerId;

    @NotBlank(message = "Training name is required")
    private String name;

    @NotNull(message = "Training type is required")
    private TrainingType type;

    @NotNull(message = "Training date is required")
    @FutureOrPresent(message = "Training date must be today or in the future")
    private LocalDate date;

    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be positive")
    private Long duration;
}
