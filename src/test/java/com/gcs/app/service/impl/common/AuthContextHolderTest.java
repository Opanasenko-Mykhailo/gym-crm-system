package com.gcs.app.service.impl.common;

import com.gcs.app.model.User;
import com.gcs.app.service.common.AuthContextHolder;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthContextHolderTest {

    private static final String AUTHENTICATED_USER = "authenticatedUser";

    @Mock
    private HttpSession httpSession;

    @InjectMocks
    private AuthContextHolder authContextHolder;

    @Test
    void setCurrentUser_and_getCurrentUser_workCorrectly() {
        User user = User.builder()
                .username("john.doe")
                .build();

        authContextHolder.setCurrentUser(user);
        User actual = authContextHolder.getCurrentUser();

        assertSame(user, actual);
        verify(httpSession).setAttribute(AUTHENTICATED_USER, user);
    }

    @Test
    void getCurrentUser_restoresFromSession_ifThreadLocalEmpty() {
        User user = User.builder()
                .username("john.doe")
                .build();

        when(httpSession.getAttribute(AUTHENTICATED_USER)).thenReturn(user);

        User actual = authContextHolder.getCurrentUser();

        assertSame(user, actual);
        verify(httpSession).getAttribute(AUTHENTICATED_USER);
    }

    @Test
    void clear_removesCurrentUserFromThreadLocal_andSession() {
        User user = User.builder()
                .username("john.doe")
                .build();

        authContextHolder.setCurrentUser(user);
        authContextHolder.clear();

        assertNull(authContextHolder.getCurrentUser());
        verify(httpSession).removeAttribute(AUTHENTICATED_USER);
    }
}
