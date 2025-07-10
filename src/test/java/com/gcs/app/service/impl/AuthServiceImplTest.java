package com.gcs.app.service.impl;

import com.gcs.app.exception.ServiceException;
import com.gcs.app.facade.dto.AuthRequestDto;
import com.gcs.app.facade.dto.AuthResponseDto;
import com.gcs.app.model.User;
import com.gcs.app.service.AuthContextHolder;
import com.gcs.app.service.CredentialsService;
import com.gcs.app.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    private static final String USERNAME = "john.doe";
    private static final String RAW_PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "$2a$10$dummyhashhere";

    @Mock
    private UserService userService;

    @Mock
    private AuthContextHolder authContextHolder;

    @Mock
    private CredentialsService credentialsService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void authenticate_whenCredentialsAreCorrect_returnsSuccessResponse() {
        AuthRequestDto dto = new AuthRequestDto();
        dto.setUsername(USERNAME);
        dto.setPassword(RAW_PASSWORD);

        User user = User.builder()
                .username(USERNAME)
                .password(ENCODED_PASSWORD)
                .build();

        when(userService.getByUsername(USERNAME)).thenReturn(user);
        when(credentialsService.isPasswordCorrect(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);

        AuthResponseDto response = authService.authenticate(dto);

        assertTrue(response.getSuccess());
        assertEquals("Login successful", response.getMessage());

        verify(authContextHolder).setCurrentUser(user);
    }

    @Test
    void authenticate_whenUserNotFound_throwsServiceException() {
        AuthRequestDto dto = new AuthRequestDto();
        dto.setUsername(USERNAME);
        dto.setPassword(RAW_PASSWORD);

        when(userService.getByUsername(USERNAME)).thenThrow(new ServiceException("User not found: " + USERNAME));

        ServiceException ex = assertThrows(ServiceException.class, () -> authService.authenticate(dto));
        assertEquals("User not found: " + USERNAME, ex.getMessage());
    }

    @Test
    void authenticate_whenPasswordIncorrect_throwsServiceException() {
        AuthRequestDto dto = new AuthRequestDto();
        dto.setUsername(USERNAME);
        dto.setPassword("wrongPassword");

        User user = User.builder()
                .username(USERNAME)
                .password(ENCODED_PASSWORD)
                .build();

        when(userService.getByUsername(USERNAME)).thenReturn(user);
        when(credentialsService.isPasswordCorrect("wrongPassword", ENCODED_PASSWORD)).thenReturn(false);

        ServiceException ex = assertThrows(ServiceException.class, () -> authService.authenticate(dto));
        assertEquals("Invalid username or password", ex.getMessage());
    }
}
