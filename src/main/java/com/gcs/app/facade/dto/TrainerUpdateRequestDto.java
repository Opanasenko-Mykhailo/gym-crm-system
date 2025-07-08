package com.gcs.app.facade.dto;

import com.gcs.app.model.TrainingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class TrainerUpdateRequestDto {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=[\\]{};':\"\\\\|,.<>/?]).{8,}$",
            message = "Password must be at least 8 characters long, contain upper and lower case letters, a digit, and a special character")
    private String password;

    @NotNull(message = "Active status is required")
    private Boolean isActive;

    @NotNull(message = "Specialization is required")
    private TrainingType specialization;
}
