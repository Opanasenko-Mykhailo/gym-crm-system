package com.gcs.app.config;

import com.gcs.app.security.AuthContextHolder;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SessionListener implements HttpSessionListener {

    private final AuthContextHolder authContextHolder;

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        authContextHolder.clear();
    }
}