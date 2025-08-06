package com.gcs.app.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilTest {

    private static final String SECRET = "12345678901234567890123456789012";

    private static final long EXPIRATION_TIME = 1000 * 60 * 60;

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() throws Exception {
        jwtUtil = new JwtUtil();
        setField(jwtUtil, "secret", SECRET);
        setField(jwtUtil, "expirationTime", EXPIRATION_TIME);
    }

    @Test
    void generateToken_and_extractUsername_shouldWork() {
        String username = "test.user";
        Set<String> roles = Set.of("ROLE_USER", "ROLE_ADMIN");

        String token = jwtUtil.generateToken(username, roles);
        String extractedUsername = jwtUtil.extractUsername(token);

        assertNotNull(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    void validateToken_validToken_returnsTrue() {
        String username = "validUser";
        Set<String> roles = Set.of("ROLE_USER");
        String token = jwtUtil.generateToken(username, roles);

        assertTrue(jwtUtil.validateToken(token, username));
    }

    @Test
    void validateToken_invalidUsername_returnsFalse() {
        String username = "validUser";
        Set<String> roles = Set.of("ROLE_USER");
        String token = jwtUtil.generateToken(username, roles);

        assertFalse(jwtUtil.validateToken(token, "otherUser"));
    }

    @Test
    void validateToken_expiredToken_returnsFalse() throws Exception {
        String username = "expiredUser";
        Set<String> roles = Set.of("ROLE_USER");
        setField(jwtUtil, "expirationTime", 0L);
        String token = jwtUtil.generateToken(username, roles);

        assertFalse(jwtUtil.validateToken(token, username));
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = JwtUtil.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}