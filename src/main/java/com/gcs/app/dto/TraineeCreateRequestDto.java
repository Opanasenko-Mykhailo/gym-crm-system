package com.gcs.app.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TraineeCreateRequestDto {
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String address;
}
