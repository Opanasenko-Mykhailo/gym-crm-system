package com.gcs.app.service.impl;

import com.gcs.app.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class AuthContextHolderImplTest {

    private final AuthContextHolderImpl authContextHolder = new AuthContextHolderImpl();

    @Test
    void setCurrentUser_and_getCurrentUser_workCorrectly() {
        User user = User.builder()
                .username("john.doe")
                .build();

        authContextHolder.setCurrentUser(user);
        User actual = authContextHolder.getCurrentUser();

        assertSame(user, actual);
    }

    @Test
    void clear_removesCurrentUser() {
        User user = User.builder()
                .username("john.doe")
                .build();

        authContextHolder.setCurrentUser(user);
        authContextHolder.clear();

        assertNull(authContextHolder.getCurrentUser());
    }
}
