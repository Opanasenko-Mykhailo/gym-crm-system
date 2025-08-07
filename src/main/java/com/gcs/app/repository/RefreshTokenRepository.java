package com.gcs.app.repository;

import com.gcs.app.model.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByToken(String tokenHash);

    void deleteByToken(String tokenHash);

    int deleteAllByExpiryDateBefore(Instant now);
}