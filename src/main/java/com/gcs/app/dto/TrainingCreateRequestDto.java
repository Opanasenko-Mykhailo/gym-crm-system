package com.gcs.app.dto;

import com.gcs.app.model.TrainingType;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;

@Data
public class TrainingCreateRequestDto {
    private Long traineeId;
    private Long trainerId;
    private String name;
    private TrainingType type;
    private LocalDate date;
    private Duration duration;
}
