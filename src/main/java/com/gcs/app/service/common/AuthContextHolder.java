package com.gcs.app.service.common;

import com.gcs.app.model.User;
import org.springframework.stereotype.Service;

@Service
public class AuthContextHolder {

    private static final ThreadLocal<User> currentUser = new ThreadLocal<>();

    public void setCurrentUser(User user) {
        currentUser.set(user);
    }

    public User getCurrentUser() {
        return currentUser.get();
    }

    public void clear() {
        currentUser.remove();
    }
}
