package com.gcs.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private Boolean isActive;
}
