package com.gcs.app.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityUtilsTest {

    private static final String TEST_USER = "testUser";
    private static final String OTHER_USER = "otherUser";

    private final SecurityUtils securityUtils = new SecurityUtils();

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;


    @Test
    void isCurrentUser_givenAuthenticatedUserWithMatchingUsername_thenReturnsTrue() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(TEST_USER);
        SecurityContextHolder.setContext(securityContext);

        boolean actual = securityUtils.isCurrentUser(TEST_USER);

        assertTrue(actual);
    }

    @Test
    void isCurrentUser_givenAuthenticatedUserWithNonMatchingUsername_thenReturnsFalse() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(OTHER_USER);
        SecurityContextHolder.setContext(securityContext);

        boolean actual = securityUtils.isCurrentUser(TEST_USER);

        assertFalse(actual);
    }

    @Test
    void isCurrentUser_givenNoAuthentication_thenReturnsFalse() {
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        boolean actual = securityUtils.isCurrentUser(TEST_USER);

        assertFalse(actual);
    }
}