package com.gcs.app.config;

import com.gcs.app.service.common.AuthContextHolder;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SessionListener implements HttpSessionListener {

    @Autowired
    private AuthContextHolder authContextHolder;

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        authContextHolder.clear();
    }
}