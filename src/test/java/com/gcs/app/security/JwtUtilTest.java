package com.gcs.app.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilTest {

    private static final String SECRET = "12345678901234567890123456789012";
    private static final long EXPIRATION_TIME = 1000 * 60 * 60;
    private static final long REFRESH_EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expirationTime", EXPIRATION_TIME);
        ReflectionTestUtils.setField(jwtUtil, "refreshExpirationTime", REFRESH_EXPIRATION_TIME);
    }

    @Test
    void generateToken_shouldReturnNonNullToken() {
        String username = "test.user";
        Set<String> roles = Set.of("ROLE_USER");

        String token = jwtUtil.generateToken(username, roles);

        assertNotNull(token);
    }

    @Test
    void generateRefreshToken_shouldReturnNonNullToken() {
        String username = "test.user";

        String token = jwtUtil.generateRefreshToken(username);

        assertNotNull(token);
        assertEquals("refresh", jwtUtil.getTokenType(token));
    }

    @Test
    void extractUsername_shouldReturnCorrectUsername() {
        String username = "test.user";
        Set<String> roles = Set.of("ROLE_USER");
        String token = jwtUtil.generateToken(username, roles);

        String extractedUsername = jwtUtil.extractUsername(token);

        assertEquals(username, extractedUsername);
    }

    @Test
    void isTokenValid_shouldReturnTrue_whenTokenIsValid() {
        String username = "validUser";
        Set<String> roles = Set.of("ROLE_USER");
        String token = jwtUtil.generateToken(username, roles);

        boolean isValid = jwtUtil.isTokenValid(token);

        assertTrue(isValid);
    }

    @Test
    void isTokenValid_shouldReturnFalse_whenTokenIsExpired() {
        String username = "expiredUser";
        Set<String> roles = Set.of("ROLE_USER");
        ReflectionTestUtils.setField(jwtUtil, "expirationTime", 0L);
        String token = jwtUtil.generateToken(username, roles);

        boolean isValid = jwtUtil.isTokenValid(token);

        assertFalse(isValid);
    }

    @Test
    void isTokenValid_shouldReturnFalse_whenTokenMalformed() {
        String badToken = "not.a.jwt.token";

        assertFalse(jwtUtil.isTokenValid(badToken));
    }

    @Test
    void getTokenType_shouldReturnNull_whenTokenMalformed() {
        String badToken = "not.a.jwt.token";

        assertNull(jwtUtil.getTokenType(badToken));
    }
}