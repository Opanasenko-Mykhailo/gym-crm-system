package com.gcs.app.security.aspect;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.gcs.app.exception.UserNotAuthenticatedException;
import com.gcs.app.model.User;
import com.gcs.app.service.AuthContextHolder;
import lombok.Getter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationAspectTest {

    @Mock
    private AuthContextHolder authContextHolder;

    @InjectMocks
    private AuthenticationAspect aspect;

    @Test
    @DisplayName("should proceed if user is authenticated")
    void shouldProceedIfUserIsAuthenticated() {
        User user = User.builder().username("john.doe").build();
        when(authContextHolder.getCurrentUser()).thenReturn(user);

        assertDoesNotThrow(() -> aspect.checkAuthentication());
    }

    @Test
    @DisplayName("should throw exception if user is not authenticated")
    void shouldThrowIfUserIsNotAuthenticated() {
        when(authContextHolder.getCurrentUser()).thenReturn(null);

        UserNotAuthenticatedException ex = assertThrows(UserNotAuthenticatedException.class, () ->
                aspect.checkAuthentication());

        assertEquals("Access denied: user is not authenticated", ex.getMessage());
    }

    @Test
    @DisplayName("should log if user was successfully authenticated")
    void shouldLogIfUserWasSuccessfullyAuthenticated() {
        User user = User.builder().username("john.doe").build();

        Logger logger = (Logger) LoggerFactory.getLogger(AuthenticationAspect.class);
        InMemoryLogAppender appender = new InMemoryLogAppender();
        appender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        appender.start();
        logger.addAppender(appender);
        when(authContextHolder.getCurrentUser()).thenReturn(user);

        aspect.checkAuthentication();

        assertTrue(appender.getLogs().stream()
                .anyMatch(e -> e.getFormattedMessage().contains("Authenticated user: john.doe")));
    }

    @Test
    @DisplayName("should not log anything if authentication attempt was unsuccessful")
    void shouldNotLogAnythingIfAuthenticationAttemptWasUnsuccessful() {
        User user = User.builder().username("john.doe").build();

        Logger logger = (Logger) LoggerFactory.getLogger(AuthenticationAspect.class);
        InMemoryLogAppender appender = new InMemoryLogAppender();
        appender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        appender.start();
        logger.addAppender(appender);
        when(authContextHolder.getCurrentUser()).thenReturn(user);

        aspect.checkAuthentication();

        assertTrue(appender.getLogs().stream()
                .anyMatch(e -> e.getFormattedMessage().contains("Authenticated user: john.doe")));
    }

    @Getter
    private static class InMemoryLogAppender extends AppenderBase<ILoggingEvent> {
        private final List<ILoggingEvent> logs = new ArrayList<>();

        @Override
        protected void append(ILoggingEvent eventObject) {
            logs.add(eventObject);
        }

    }
}
