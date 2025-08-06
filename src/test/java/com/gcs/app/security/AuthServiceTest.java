package com.gcs.app.security;

import com.gcs.app.exception.ServiceException;
import com.gcs.app.exception.UserNotAuthorizedException;
import com.gcs.app.facade.dto.AuthRequestDto;
import com.gcs.app.facade.dto.AuthResponseDto;
import com.gcs.app.model.Role;
import com.gcs.app.model.User;
import com.gcs.app.model.enums.RoleType;
import com.gcs.app.service.UserService;
import com.gcs.app.service.common.CredentialsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anySet;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String USERNAME = "rowan.atkinson";
    private static final String RAW_PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "$2a$10$dummyhashhere";
    private static final String DUMMY_TOKEN = "dummy.jwt.token";

    @Mock
    private UserService userService;

    @Mock
    private CredentialsService credentialsService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void authenticate_whenCredentialsAreCorrect_returnsSuccessResponse() {
        AuthRequestDto dto = new AuthRequestDto();
        dto.setUsername(USERNAME);
        dto.setPassword(RAW_PASSWORD);

        User user = User.builder()
                .username(USERNAME)
                .password(ENCODED_PASSWORD)
                .isActive(true)
                .roles(Set.of(new Role(1L, RoleType.ROLE_TRAINEE)))
                .build();

        when(userService.getByUsername(USERNAME)).thenReturn(user);
        when(credentialsService.isPasswordCorrect(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(jwtUtil.generateToken(eq(USERNAME), anySet())).thenReturn(DUMMY_TOKEN);

        AuthResponseDto response = authService.authenticate(dto);

        assertTrue(response.getSuccess());
        assertEquals(DUMMY_TOKEN, response.getAccessToken());

        verify(userService).getByUsername(USERNAME);
        verify(credentialsService).isPasswordCorrect(RAW_PASSWORD, ENCODED_PASSWORD);
        verify(jwtUtil).generateToken(eq(USERNAME), anySet());
    }

    @Test
    void authenticate_whenUserIsInactive_throwsUserNotAuthorizedException() {
        AuthRequestDto dto = new AuthRequestDto();
        dto.setUsername(USERNAME);
        dto.setPassword(RAW_PASSWORD);

        User user = User.builder()
                .username(USERNAME)
                .password(ENCODED_PASSWORD)
                .isActive(false)
                .build();

        when(userService.getByUsername(USERNAME)).thenReturn(user);

        UserNotAuthorizedException ex = assertThrows(UserNotAuthorizedException.class,
                () -> authService.authenticate(dto));

        assertEquals("User is inactive", ex.getMessage());
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
    void authenticate_whenPasswordIncorrect_throwsUserNotAuthorizedException() {
        AuthRequestDto dto = new AuthRequestDto();
        dto.setUsername(USERNAME);
        dto.setPassword("wrongPassword");

        User user = User.builder()
                .username(USERNAME)
                .password(ENCODED_PASSWORD)
                .isActive(true)
                .build();

        when(userService.getByUsername(USERNAME)).thenReturn(user);
        when(credentialsService.isPasswordCorrect("wrongPassword", ENCODED_PASSWORD)).thenReturn(false);

        UserNotAuthorizedException ex = assertThrows(UserNotAuthorizedException.class, () -> authService.authenticate(dto));

        assertEquals("Invalid username or password", ex.getMessage());
    }
}