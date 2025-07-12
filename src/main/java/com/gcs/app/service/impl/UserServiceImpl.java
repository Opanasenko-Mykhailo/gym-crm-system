package com.gcs.app.service.impl;

import com.gcs.app.dao.UserDao;
import com.gcs.app.exception.ServiceException;
import com.gcs.app.facade.dto.PasswordChangeRequestDto;
import com.gcs.app.model.User;
import com.gcs.app.service.UserService;
import com.gcs.app.service.common.CredentialsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final CredentialsService credentialsService;

    @Override
    public User getByUsername(String username) {
        return userDao.findByUsername(username)
                .orElseThrow(() -> new ServiceException("User not found: " + username));
    }

    @Override
    public Set<String> getAllUsernames() {
        return userDao.findAllUsernames();
    }

    @Override
    public void changePassword(@Valid PasswordChangeRequestDto dto) {
        log.info("Changing password for username: {}", dto.getUsername());
        User user = getByUsername(dto.getUsername());

        if (!credentialsService.isPasswordCorrect(dto.getOldPassword(), user.getPassword())) {
            throw new ServiceException("Old password is incorrect");
        }

        String encodedNewPassword = credentialsService.encodePassword(dto.getNewPassword());

        User updatedUser = user.toBuilder()
                .password(encodedNewPassword)
                .build();

        userDao.update(updatedUser);
        log.info("Password changed successfully for username: {}", dto.getUsername());
    }
}
