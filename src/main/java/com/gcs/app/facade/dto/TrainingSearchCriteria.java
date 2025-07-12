package com.gcs.app.facade.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public abstract class TrainingSearchCriteria {
    private String username;
    private LocalDate fromDate;
    private LocalDate toDate;
}
