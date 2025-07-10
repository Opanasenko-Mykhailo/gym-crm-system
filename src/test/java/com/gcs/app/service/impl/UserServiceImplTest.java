package com.gcs.app.service.impl;

import com.gcs.app.dao.UserDao;
import com.gcs.app.exception.ServiceException;
import com.gcs.app.facade.dto.PasswordChangeRequestDto;
import com.gcs.app.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private static final String USERNAME = "john.doe";
    private static final String RAW_PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = new BCryptPasswordEncoder().encode(RAW_PASSWORD);
    private static final String NEW_PASSWORD = "NewPassword123!";

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserServiceImpl service;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void getAllUsernames_returnsSetOfUsernames() {
        Set<String> expected = Set.of("john.doe", "jane.smith");

        when(userDao.findAllUsernames()).thenReturn(expected);

        Set<String> actual = service.getAllUsernames();

        assertEquals(expected, actual);
        verify(userDao).findAllUsernames();
    }

    @Test
    void changePassword_whenOldPasswordMatches_updatesPassword() {
        PasswordChangeRequestDto dto = new PasswordChangeRequestDto();
        dto.setUsername(USERNAME);
        dto.setOldPassword(RAW_PASSWORD);
        dto.setNewPassword(NEW_PASSWORD);

        User existingUser = User.builder()
                .username(USERNAME)
                .password(ENCODED_PASSWORD)
                .build();

        when(userDao.findByUsername(USERNAME)).thenReturn(Optional.of(existingUser));

        service.changePassword(dto);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userDao).update(captor.capture());

        User updatedUser = captor.getValue();

        assertTrue(passwordEncoder.matches(NEW_PASSWORD, updatedUser.getPassword()));
        verify(userDao).findByUsername(USERNAME);
    }

    @Test
    void changePassword_whenUserNotFound_throwsServiceException() {
        PasswordChangeRequestDto dto = new PasswordChangeRequestDto();
        dto.setUsername(USERNAME);
        dto.setOldPassword(RAW_PASSWORD);
        dto.setNewPassword(NEW_PASSWORD);

        when(userDao.findByUsername(USERNAME)).thenReturn(Optional.empty());

        ServiceException ex = assertThrows(ServiceException.class, () -> service.changePassword(dto));

        assertEquals("User not found: " + USERNAME, ex.getMessage());
        verify(userDao).findByUsername(USERNAME);
    }

    @Test
    void changePassword_whenOldPasswordIncorrect_throwsServiceException() {
        PasswordChangeRequestDto dto = new PasswordChangeRequestDto();
        dto.setUsername(USERNAME);
        dto.setOldPassword("wrongPassword");
        dto.setNewPassword(NEW_PASSWORD);

        User existingUser = User.builder()
                .username(USERNAME)
                .password(ENCODED_PASSWORD)
                .build();

        when(userDao.findByUsername(USERNAME)).thenReturn(Optional.of(existingUser));

        ServiceException ex = assertThrows(ServiceException.class, () -> service.changePassword(dto));

        assertEquals("Old password is incorrect", ex.getMessage());
        verify(userDao).findByUsername(USERNAME);
        verify(userDao, never()).update(any());
    }
}
