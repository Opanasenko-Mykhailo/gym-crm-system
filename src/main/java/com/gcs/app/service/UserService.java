package com.gcs.app.service;

import com.gcs.app.facade.dto.PasswordChangeRequestDto;
import com.gcs.app.model.User;

import java.util.Set;

public interface UserService {
    User getByUsername(String username);
    Set<String> getAllUsernames();
    void changePassword(PasswordChangeRequestDto dto);
}
