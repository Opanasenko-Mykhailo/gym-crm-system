package com.gcs.app.aspect;

import com.gcs.app.exception.ServiceException;
import com.gcs.app.model.User;
import com.gcs.app.service.AuthContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationAspect {

    private final AuthContextHolder authContextHolder;

    @Before("@annotation(com.gcs.app.security.Authenticated)")
    public void checkAuthentication() {
        User currentUser = authContextHolder.getCurrentUser();

        if (currentUser == null) {
            throw new ServiceException("Access denied: user is not authenticated");
        }

        log.debug("Authenticated user: {}", currentUser.getUsername());
    }
}
