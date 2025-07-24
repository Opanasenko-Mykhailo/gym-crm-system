package com.gcs.app.security;

import com.gcs.app.model.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthContextHolder {

    private static final ThreadLocal<User> currentUser = new ThreadLocal<>();

    private final HttpSession httpSession;

    public void setCurrentUser(User user) {
        currentUser.set(user);
        httpSession.setAttribute("authenticatedUser", user);
    }

    public User getCurrentUser() {
        User user = currentUser.get();

        return user != null
                ? user
                : restoreFromSession();
    }

    public void clear() {
        currentUser.remove();
        httpSession.removeAttribute("authenticatedUser");
    }

    private User restoreFromSession() {
        User sessionUser = (User) httpSession.getAttribute("authenticatedUser");

        if (sessionUser != null) {
            currentUser.set(sessionUser);
        }

        return sessionUser;
    }
}