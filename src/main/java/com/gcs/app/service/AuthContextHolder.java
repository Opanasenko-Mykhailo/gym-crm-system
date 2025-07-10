package com.gcs.app.service;

import com.gcs.app.model.User;

public interface AuthContextHolder {
    void setCurrentUser(User user);
    User getCurrentUser();
    void clear();
}
