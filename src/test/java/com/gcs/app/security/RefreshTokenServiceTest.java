package com.gcs.app.security;

import com.gcs.app.exception.RefreshTokenNotFoundException;
import com.gcs.app.model.RefreshTokenEntity;
import com.gcs.app.repository.RefreshTokenRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    private static final String RAW_TOKEN = "raw-refresh-token";
    private static final String USERNAME = "test.user";
    private static final long EXPIRATION_MS = 3600000L;
    private static final String HASHED_TOKEN = hash();

    @Mock
    private RefreshTokenRepository repository;

    @InjectMocks
    private RefreshTokenService service;

    @Captor
    private ArgumentCaptor<RefreshTokenEntity> captor;

    @Test
    void saveToken_shouldSaveHashedToken() {
        ReflectionTestUtils.setField(service, "refreshExpirationTime", EXPIRATION_MS);

        service.saveToken(RAW_TOKEN, USERNAME);

        verify(repository).save(captor.capture());
        RefreshTokenEntity saved = captor.getValue();
        assertEquals(USERNAME, saved.getUsername());
        assertEquals(HASHED_TOKEN, saved.getToken());
        assertTrue(saved.getExpiryDate().isAfter(Instant.now()));
    }

    @Test
    void getUsername_existingToken_returnsUsername() {
        RefreshTokenEntity entity = RefreshTokenEntity.builder()
                .username(USERNAME)
                .token(HASHED_TOKEN)
                .expiryDate(Instant.now().plusSeconds(3600))
                .build();

        when(repository.findByToken(HASHED_TOKEN)).thenReturn(Optional.of(entity));

        String actual = service.getUsername(RAW_TOKEN);

        assertEquals(USERNAME, actual);
        verify(repository).findByToken(HASHED_TOKEN);
    }

    @Test
    void getUsername_nonExistingToken_throwsRefreshTokenNotFoundException() {
        when(repository.findByToken(HASHED_TOKEN)).thenReturn(Optional.empty());

        RefreshTokenNotFoundException ex = assertThrows(
                RefreshTokenNotFoundException.class, () -> service.getUsername(RAW_TOKEN));

        assertEquals("Token not found", ex.getMessage());
        verify(repository).findByToken(HASHED_TOKEN);
    }

    @Test
    void invalidateToken_shouldCallDeleteByToken() {
        service.invalidateToken(RAW_TOKEN);

        verify(repository).deleteByToken(HASHED_TOKEN);
    }

    @SneakyThrows
    private static String hash() {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashedBytes = digest.digest(RefreshTokenServiceTest.RAW_TOKEN.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hashedBytes);
    }
}