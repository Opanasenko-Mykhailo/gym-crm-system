package com.gcs.app.security;

import com.gcs.app.model.RefreshTokenEntity;
import com.gcs.app.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

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

    @Mock
    private RefreshTokenRepository repository;

    @InjectMocks
    private RefreshTokenService service;

    @Captor
    private ArgumentCaptor<RefreshTokenEntity> captor;

    @Test
    void saveToken_shouldSaveHashedToken() {
        setExpirationTime();

        service.saveToken(RAW_TOKEN, USERNAME);

        verify(repository).save(captor.capture());
        RefreshTokenEntity saved = captor.getValue();
        assertEquals(USERNAME, saved.getUsername());
        assertEquals(hash(), saved.getToken());
        assertTrue(saved.getExpiryDate().isAfter(Instant.now()));
    }

    @Test
    void getUsername_existingToken_returnsUsername() {
        String hashed = hash();
        RefreshTokenEntity entity = RefreshTokenEntity.builder()
                .username(USERNAME)
                .token(hashed)
                .expiryDate(Instant.now().plusSeconds(3600))
                .build();

        when(repository.findByToken(hashed)).thenReturn(Optional.of(entity));

        String actual = service.getUsername(RAW_TOKEN);

        assertEquals(USERNAME, actual);
        verify(repository).findByToken(hashed);
    }

    @Test
    void getUsername_nonExistingToken_throwsRuntimeException() {
        String hashed = hash();
        when(repository.findByToken(hashed)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.getUsername(RAW_TOKEN));
        assertEquals("Token not found", ex.getMessage());

        verify(repository).findByToken(hashed);
    }

    @Test
    void invalidateToken_shouldCallDeleteByToken() {
        String hashed = hash();

        service.invalidateToken(RAW_TOKEN);

        verify(repository).deleteByToken(hashed);
    }

    private void setExpirationTime() {
        ReflectionTestUtils.setField(service, "refreshExpirationTime", 3600000L);
    }

    private String hash() {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(RefreshTokenServiceTest.RAW_TOKEN.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}