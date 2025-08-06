package com.gcs.app.security;

import com.gcs.app.exception.UserNotAuthorizedException;
import com.gcs.app.facade.dto.AuthRequestDto;
import com.gcs.app.facade.dto.AuthResponseDto;
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

        String token = jwtUtil.generateToken(user.getUsername(), roles);

        log.info("User {} authenticated successfully", dto.getUsername());

        return new AuthResponseDto(true, token);
    }
}