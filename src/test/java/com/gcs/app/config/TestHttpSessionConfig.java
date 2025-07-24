package com.gcs.app.config;

import jakarta.servlet.http.HttpSession;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestHttpSessionConfig {

    @Bean
    public HttpSession httpSession() {
        return Mockito.mock(HttpSession.class);
    }
}
