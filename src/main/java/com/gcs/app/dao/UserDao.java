package com.gcs.app.dao;

import com.gcs.app.model.User;

import java.util.Optional;
import java.util.Set;

public interface UserDao {
    User update(User user);
    Set<String> findAllUsernames();
    Optional<User> findByUsername(String username);
}
