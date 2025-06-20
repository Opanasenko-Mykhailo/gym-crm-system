package com.gcs.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Training {
    private Long traineeId;
    private Long trainerId;
    private String name;
    private TrainingType type;
    private LocalDate date;
    private Double duration;
}
