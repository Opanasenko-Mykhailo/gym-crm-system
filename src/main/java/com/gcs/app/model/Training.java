package com.gcs.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Duration;
import java.time.LocalDate;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Builder(toBuilder = true)
public class Training {
    private final Long id;
    private final Long traineeId;
    private final Long trainerId;
    private final String name;
    private final TrainingType type;
    private final LocalDate date;
    private final Duration duration;
}
