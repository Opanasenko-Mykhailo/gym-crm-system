package com.gcs.app.logging.aspect;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestLoggingAspectTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    @InjectMocks
    private RestLoggingAspect aspect;

    @Test
    @DisplayName("Should log REST request with method and args")
    void shouldLogRequest() throws NoSuchMethodException {
        InMemoryLogAppender appender = createAndAttachAppender();
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(RestLoggingAspect.class.getDeclaredMethod("logRequest", JoinPoint.class));
        when(joinPoint.getArgs()).thenReturn(new Object[]{"arg1", 123});

        aspect.logRequest(joinPoint);

        assertTrue(appender.getLogs().stream()
                .anyMatch(event -> event.getFormattedMessage().contains("REST Request [POST /api/test]")), "Should contain HTTP method and URI");
        assertTrue(appender.getLogs().stream()
                .anyMatch(event -> event.getFormattedMessage().contains("logRequest")), "Should log method name");
        assertTrue(appender.getLogs().stream()
                .anyMatch(event -> event.getFormattedMessage().contains("[arg1, 123]")), "Should log method arguments");
    }

    @Test
    @DisplayName("Should log REST response")
    void shouldLogResponse() {
        InMemoryLogAppender appender = createAndAttachAppender();
        String response = "responseData";

        aspect.logResponse(response);

        assertTrue(appender.getLogs().stream()
                .anyMatch(event -> event.getFormattedMessage().contains("REST Response: " + response)), "Should log response");
    }

    @Test
    @DisplayName("Should log REST error")
    void shouldLogException() {
        InMemoryLogAppender appender = createAndAttachAppender();
        Exception ex = new RuntimeException("test exception");

        aspect.logException(ex);

        assertTrue(appender.getLogs().stream()
                .anyMatch(event -> event.getFormattedMessage().contains("REST Error: test exception")), "Should log exception message");
    }

    private InMemoryLogAppender createAndAttachAppender() {
        Logger logger = (Logger) LoggerFactory.getLogger(RestLoggingAspect.class);
        InMemoryLogAppender appender = new InMemoryLogAppender();
        appender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        appender.start();
        logger.addAppender(appender);
        return appender;
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