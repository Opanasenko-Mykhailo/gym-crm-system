package com.gcs.app.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("securityUtils")
public class SecurityUtils {
    public boolean isCurrentUser(String username) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return auth != null && username.equals(auth.getName());
    }
}