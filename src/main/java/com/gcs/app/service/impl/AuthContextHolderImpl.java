package com.gcs.app.service.impl;

import com.gcs.app.model.User;
import com.gcs.app.service.AuthContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthContextHolderImpl implements AuthContextHolder {

    private static final ThreadLocal<User> currentUser = new ThreadLocal<>();

    @Override
    public void setCurrentUser(User user) {
        currentUser.set(user);
    }

    @Override
    public User getCurrentUser() {
        return currentUser.get();
    }

    @Override
    public void clear() {
        currentUser.remove();
    }
}
