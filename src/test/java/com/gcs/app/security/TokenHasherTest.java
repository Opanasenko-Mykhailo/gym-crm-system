package com.gcs.app.security;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TokenHasherTest {

    private final TokenHasher hasher = new TokenHasher();

    @Test
    void hash_shouldReturnCorrectSha256Base64() throws Exception {
        String raw = "raw-refresh-token";

        String actualHash = hasher.hash(raw);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] expectedBytes = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
        String expectedHash = Base64.getEncoder().encodeToString(expectedBytes);
        assertEquals(expectedHash, actualHash);
    }
}