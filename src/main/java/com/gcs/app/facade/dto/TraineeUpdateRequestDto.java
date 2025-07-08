package com.gcs.app.facade.dto;

import com.gcs.app.facade.constant.ValidationPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TraineeUpdateRequestDto {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    @Pattern(
            regexp = ValidationPatterns.PASSWORD_PATTERN,
            message = "Password must be at least 8 characters long, contain upper and lower case letters, a digit, and a special character"
    )
    private String password;

    @NotNull(message = "Active status is required")
    private Boolean isActive;

    @NotNull(message = "Date of birth is required")
    @PastOrPresent(message = "Date of birth must be in the past or today")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 255, message = "Address must be between 5 and 255 characters")
    private String address;
}
