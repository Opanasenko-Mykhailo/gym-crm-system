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

        if (!user.getIsActive()) {
            throw new UserNotAuthorizedException("User is inactive");
        }

        if (!credentialsService.isPasswordCorrect(dto.getPassword(), user.getPassword())) {
            throw new UserNotAuthorizedException("Invalid username or password");
        }

        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getRoleType().name())
                .collect(Collectors.toSet());

        String accessToken = jwtUtil.generateToken(user.getUsername(), roles);
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());
        refreshTokenService.saveToken(refreshToken, user.getUsername());

        log.info("User {} authenticated successfully", dto.getUsername());

        return new AuthResponseDto(true, accessToken, refreshToken);
    }

    public AuthResponseDto refreshToken(@Valid RefreshTokenRequestDto request) {
        String refreshToken = request.getRefreshToken();
        if (!jwtUtil.isTokenValid(refreshToken)) {
            throw new UserNotAuthorizedException("Invalid or expired refresh token");
        }

        String username = refreshTokenService.getUsername(refreshToken);
        if (username == null) {
            throw new UserNotAuthorizedException("Invalid refresh token");
        }

        String tokenUsername = jwtUtil.extractUsername(refreshToken);
        if (!tokenUsername.equals(username)) {
            throw new UserNotAuthorizedException("Invalid refresh token");
        }

        User user = userService.getByUsername(username);
        if (!user.getIsActive()) {
            throw new UserNotAuthorizedException("User is inactive");
        }

        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getRoleType().name())
                .collect(Collectors.toSet());

        String newAccessToken = jwtUtil.generateToken(user.getUsername(), roles);
        log.info("New access token generated for user {}", username);

        return new AuthResponseDto(true, newAccessToken, refreshToken);
    }

    public void logout(@Valid LogoutRequestDto request) {
        String refreshToken = request.getRefreshToken();
        if (!jwtUtil.isTokenValid(refreshToken)) {
            throw new UserNotAuthorizedException("Invalid or expired refresh token");
        }

        String username = refreshTokenService.getUsername(refreshToken);
        if (username == null) {
            throw new UserNotAuthorizedException("Invalid refresh token");
        }

        refreshTokenService.invalidateToken(refreshToken);
        log.info("User {} logged out successfully", username);
    }
}