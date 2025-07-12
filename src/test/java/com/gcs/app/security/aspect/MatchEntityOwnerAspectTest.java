package com.gcs.app.security.aspect;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.gcs.app.model.User;
import com.gcs.app.security.MatchEntityOwner;
import com.gcs.app.service.common.AuthContextHolder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatchEntityOwnerAspectTest {

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private MatchEntityOwner annotation;

    @Mock
    private MethodSignature signature;

    @Mock
    private AuthContextHolder authContextHolder;

    @InjectMocks
    private MatchEntityOwnerAspect aspect;

    @Test
    @DisplayName("should successfully pass validation if current username match to entity owner")
    void shouldSuccessfullyPassValidationIfCurrentUserNameMatchToEntityOwner() {
        User currentUser = User.builder().username("entity.owner").build();
        String ownerUsername = "entity.owner";

        when(annotation.usernameParam()).thenReturn("traineeUpdateRequestDto");
        when(authContextHolder.getCurrentUser()).thenReturn(currentUser);
        when(signature.getParameterNames()).thenReturn(new String[]{"traineeUpdateRequestDto"});
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{new CorrectDtoMock(ownerUsername)});

        assertDoesNotThrow(() -> aspect.checkUsernameMatch(joinPoint, annotation));
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {"   ", "          ", " "})
    @DisplayName("should throw exception when current user's username is blank")
    void shouldThrowExceptionWhenCurrentUserUsernameIsBlank(String blankUsername) {
        User user = User.builder().username(blankUsername).build();
        when(authContextHolder.getCurrentUser()).thenReturn(user);

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () ->
                aspect.checkUsernameMatch(joinPoint, annotation));

        assertEquals("User was not authenticated as current session User or his username is missing", ex.getMessage());
    }

    @Test
    @DisplayName("should throw exception when current user is null")
    void shouldThrowExceptionWhenCurrentUserIsNull() {
        when(authContextHolder.getCurrentUser()).thenReturn(null);

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () ->
                aspect.checkUsernameMatch(joinPoint, annotation));

        assertEquals("User was not authenticated as current session User or his username is missing", ex.getMessage());
    }


    @Test
    @DisplayName("should throw exception when username does not match to entity owner")
    void shouldThrowExceptionWhenUsernameDoesNotMatchToEntityOwner() {
        User user = User.builder().username("john.doe").build();

        when(annotation.usernameParam()).thenReturn("traineeUpdateRequestDto");
        when(authContextHolder.getCurrentUser()).thenReturn(user);
        when(signature.getParameterNames()).thenReturn(new String[]{"traineeUpdateRequestDto"});
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{new CorrectDtoMock("entity.owner")});

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () ->
                aspect.checkUsernameMatch(joinPoint, annotation));

        assertEquals("User john.doe has not permission for operation regarding entity.owner entity", ex.getMessage());
    }

    @Test
    @DisplayName("should throw exception when owner username is missing")
    void shouldThrowExceptionWhenOwnerUsernameIsMissing() {
        User user = User.builder().username("john.doe").build();

        when(annotation.usernameParam()).thenReturn("traineeUpdateRequestDto");
        when(authContextHolder.getCurrentUser()).thenReturn(user);
        when(signature.getParameterNames()).thenReturn(new String[]{"traineeUpdateRequestDto"});
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{new InvalidDtoMock("address")});

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () ->
                aspect.checkUsernameMatch(joinPoint, annotation));

        assertEquals("Username parameter is null or missing", ex.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {" ", "      ", "   "})
    @DisplayName("should throw exception when owner username is blank")
    void shouldThrowExceptionWhenOwnerUsernameIsBlank(String blankOwnerUsername) {
        User user = User.builder().username("john.doe").build();

        when(annotation.usernameParam()).thenReturn("traineeUpdateRequestDto");
        when(authContextHolder.getCurrentUser()).thenReturn(user);
        when(signature.getParameterNames()).thenReturn(new String[]{"traineeUpdateRequestDto"});
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{new CorrectDtoMock(blankOwnerUsername)});

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () ->
                aspect.checkUsernameMatch(joinPoint, annotation));

        assertEquals("Username parameter is null or missing", ex.getMessage());
    }

    @Test
    @DisplayName("should log if authentication attempt was successful")
    void shouldLogIfAuthenticationAttemptWasSuccessful() throws Throwable {
        User currentUser = User.builder().username("entity.owner").build();
        String ownerUsername = "entity.owner";

        Logger logger = (Logger) LoggerFactory.getLogger(MatchEntityOwnerAspect.class);
        InMemoryLogAppender appender = new InMemoryLogAppender();
        appender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        appender.start();
        logger.addAppender(appender);

        when(annotation.usernameParam()).thenReturn("traineeUpdateRequestDto");
        when(authContextHolder.getCurrentUser()).thenReturn(currentUser);
        when(signature.getParameterNames()).thenReturn(new String[]{"traineeUpdateRequestDto"});
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{new CorrectDtoMock(ownerUsername)});

        aspect.checkUsernameMatch(joinPoint, annotation);

        Optional<ILoggingEvent> actualEvent = appender.getLogs().stream().findFirst();
        assertTrue(actualEvent.isPresent());
        assertTrue(actualEvent.get().getFormattedMessage().contains("User entity.owner has permission to requested operation"));
    }

    @Getter
    @RequiredArgsConstructor
    private static class CorrectDtoMock {
        private final String username;
    }

    @Getter
    @RequiredArgsConstructor
    private static class InvalidDtoMock {
        private final String someField;
    }

    private static class FacadeMock {
        @MatchEntityOwner(usernameParam = "traineeUpdateRequestDto")
        public void updateTrainee(InvalidDtoMock dto) {

        }
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
