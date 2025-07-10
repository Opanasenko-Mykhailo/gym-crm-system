package com.gcs.app.service.impl;

import com.gcs.app.exception.ServiceException;
import com.gcs.app.facade.dto.AuthRequestDto;
import com.gcs.app.facade.dto.AuthResponseDto;
import com.gcs.app.model.User;
import com.gcs.app.service.AuthContextHolder;
import com.gcs.app.service.AuthService;
import com.gcs.app.service.CredentialsService;
import com.gcs.app.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final CredentialsService credentialsService;
    private final AuthContextHolder authContextHolder;

    @Override
    public AuthResponseDto authenticate(@Valid AuthRequestDto dto) {
        User user = userService.getByUsername(dto.getUsername());

        if (!credentialsService.isPasswordCorrect(dto.getPassword(), user.getPassword())) {
            throw new ServiceException("Invalid username or password");
        }

        authContextHolder.setCurrentUser(user);

        return new AuthResponseDto(true, "Login successful");
    }
}
