package com.gcs.app.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TraineeDto {
    private Long userId;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private Boolean isActive;
    private LocalDate dateOfBirth;
    private String address;
}
