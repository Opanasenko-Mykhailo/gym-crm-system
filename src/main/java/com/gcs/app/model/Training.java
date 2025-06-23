package com.gcs.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Training {
    private Long id;
    private Long traineeId;
    private Long trainerId;
    private String name;
    private TrainingType type;
    private LocalDate date;
    private Duration duration;
}
