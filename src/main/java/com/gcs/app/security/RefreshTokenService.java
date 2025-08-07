package com.gcs.app.security;

import com.gcs.app.model.RefreshTokenEntity;
import com.gcs.app.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpirationTime;

    @Transactional
    public void saveToken(String token, String username) {
        Instant expiry = Instant.now().plusMillis(refreshExpirationTime);

        String hashed = hash(token);
        RefreshTokenEntity entity = RefreshTokenEntity.builder()
                .token(hashed)
                .username(username)
                .expiryDate(expiry)
                .build();

        repository.save(entity);
    }

    @Transactional(readOnly = true)
    public String getUsername(String token) {
        String hashed = hash(token);

        return repository.findByToken(hashed)
                .map(RefreshTokenEntity::getUsername)
                .orElseThrow(() -> new RuntimeException("Token not found"));
    }

    @Transactional
    public void invalidateToken(String token) {
        String hashed = hash(token);
        repository.deleteByToken(hashed);
    }

    private String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(token.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
