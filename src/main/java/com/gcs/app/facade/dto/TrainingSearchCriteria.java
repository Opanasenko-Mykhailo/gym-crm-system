package com.gcs.app.facade.dto;

import java.time.LocalDate;

public interface TrainingSearchCriteria {
    String getUsername();
    LocalDate getFromDate();
    LocalDate getToDate();
}
