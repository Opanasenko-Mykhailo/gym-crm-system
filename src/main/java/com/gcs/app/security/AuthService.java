package com.gcs.app.security;

import com.gcs.app.exception.UserNotAuthorizedException;
import com.gcs.app.facade.dto.AuthRequestDto;
import com.gcs.app.facade.dto.AuthResponseDto;
import com.gcs.app.facade.dto.LogoutRequestDto;
import com.gcs.app.facade.dto.RefreshTokenRequestDto;
import com.gcs.app.model.User;
import com.gcs.app.service.UserService;
import com.gcs.app.service.common.CredentialsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class AuthService {

    private final UserService userService;
    private final CredentialsService credentialsService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;

    public AuthResponseDto authenticate(@Valid AuthRequestDto dto) {
        User user = userService.getByUsername(dto.getUsername());

        validateUserIsActive(user);

        if (!credentialsService.isPasswordCorrect(dto.getPassword(), user.getPassword())) {
            throw new UserNotAuthorizedException("Invalid username or password");
        }

        String accessToken = jwtUtil.generateToken(user.getUsername(), extractRoleNames(user));
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());
        refreshTokenService.saveToken(refreshToken, user.getUsername());

        log.info("User {} authenticated successfully", dto.getUsername());

        return new AuthResponseDto(true, accessToken, refreshToken);
    }

    public AuthResponseDto refreshToken(@Valid RefreshTokenRequestDto request) {
        String refreshToken = request.getRefreshToken();

        String username = resolveAndValidateUsernameFromToken(refreshToken);
        User user = userService.getByUsername(username);
        validateUserIsActive(user);

        String newAccessToken = jwtUtil.generateToken(user.getUsername(), extractRoleNames(user));
        log.info("New access token generated for user {}", username);

        return new AuthResponseDto(true, newAccessToken, refreshToken);
    }

    public void logout(@Valid LogoutRequestDto request) {
        String refreshToken = request.getRefreshToken();

        String username = resolveAndValidateUsernameFromToken(refreshToken);
        refreshTokenService.invalidateToken(refreshToken);

        log.info("User {} logged out successfully", username);
    }

    private String resolveAndValidateUsernameFromToken(String refreshToken) {
        isValidRefreshTokenOrThrow(refreshToken);

        String dbUsername = refreshTokenService.getUsername(refreshToken);
        if (dbUsername == null) {
            throw new UserNotAuthorizedException("Invalid refresh token");
        }

        String tokenUsername = jwtUtil.extractUsername(refreshToken);
        if (!tokenUsername.equals(dbUsername)) {
            throw new UserNotAuthorizedException("Invalid refresh token");
        }

        return dbUsername;
    }

    private void isValidRefreshTokenOrThrow(String token) {
        if (!jwtUtil.isTokenValid(token)) {
            throw new UserNotAuthorizedException("Invalid or expired refresh token");
        }
    }

    private void validateUserIsActive(User user) {
        if (!user.getIsActive()) {
            throw new UserNotAuthorizedException("User is inactive");
        }
    }

    private Set<String> extractRoleNames(User user) {
        return user.getRoles().stream()
                .map(role -> role.getRoleType().name())
                .collect(Collectors.toSet());
    }
}