package com.gcs.app.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
@SuperBuilder(toBuilder = true)
public class TrainingType {
    private final String name;
}