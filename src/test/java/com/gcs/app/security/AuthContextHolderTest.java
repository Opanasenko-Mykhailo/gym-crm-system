package com.gcs.app.security;

import com.gcs.app.model.User;
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

    private static final String USERNAME = "rowan.atkinson";

    private final User user = createUser();

    @Mock
    private HttpSession httpSession;

    @InjectMocks
    private AuthContextHolder authContextHolder;

    @Test
    void setCurrentUser_storesUserInThreadLocalAndSession() {
        authContextHolder.setCurrentUser(user);

        assertSame(user, authContextHolder.getCurrentUser());
        verify(httpSession).setAttribute("authenticatedUser", user);
    }

    @Test
    void getCurrentUser_restoresUserFromSessionIfThreadLocalEmpty() {
        when(httpSession.getAttribute("authenticatedUser")).thenReturn(user);

        User actual = authContextHolder.getCurrentUser();

        assertSame(user, actual);
    }

    @Test
    void clear_removesUserFromThreadLocalAndSession() {
        authContextHolder.setCurrentUser(user);

        authContextHolder.clear();

        assertNull(authContextHolder.getCurrentUser());
        verify(httpSession).removeAttribute("authenticatedUser");
    }

    private User createUser() {
        return User.builder()
                .username(USERNAME)
                .build();
    }
}
