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
public class User {
    private final String firstName;
    private final String lastName;
    private final String username;
    @ToString.Exclude
    private final String password;
    private final Boolean isActive;
}
