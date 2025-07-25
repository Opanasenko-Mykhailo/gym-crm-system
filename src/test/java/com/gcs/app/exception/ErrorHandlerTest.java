package com.gcs.app.exception;

import com.gcs.app.rest.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.stream.Stream;

import static com.gcs.app.exception.ApiError.AUTHENTICATION_ERROR;
import static com.gcs.app.exception.ApiError.DATABASE_ERROR;
import static com.gcs.app.exception.ApiError.INVALID_REQUEST_ERROR;
import static com.gcs.app.exception.ApiError.NOT_FOUND_ERROR;
import static com.gcs.app.exception.ApiError.SERVER_ERROR;
import static com.gcs.app.exception.ApiError.VALIDATION_ERROR;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@ExtendWith(MockitoExtension.class)
class ErrorHandlerTest {

    private static final String VALIDATION_MSG = "Username must not be blank";
    private static final String ENTITY_NOT_FOUND_MSG = "Trainee with ID 10 was not found in the database";
    private static final String UNKNOWN_SERVICE_MSG = "Unexpected error occurred during trainee registration";
    private static final String DAO_ERROR_MSG = "Failed to execute database query for trainee entity";
    private static final String AUTH_ERROR_MSG = "Authentication token is missing or invalid";
    private static final String UNHANDLED_ERROR_MSG = "Unhandled exception occurred while processing request";

    @InjectMocks
    private ErrorHandler errorHandler;

    @ParameterizedTest
    @MethodSource("invalidRequestProvider")
    void handleServiceException_whenPrefixMatches_returnsInvalidRequestError(String errorMessage) {
        ServiceException ex = new ServiceException(errorMessage);
        String expectedMessage = format(INVALID_REQUEST_ERROR.getMessage() + errorMessage);

        ResponseEntity<ErrorResponse> result = errorHandler.handleServiceException(ex);

        assertNotNull(result.getBody());
        assertEquals(BAD_REQUEST, result.getStatusCode());
        assertEquals(INVALID_REQUEST_ERROR.getCode(), result.getBody().getErrorCode());
        assertEquals(expectedMessage, result.getBody().getErrorMessage());
    }

    @Test
    void handleServiceException_whenPrefixUnknown_returnsServerError() {
        ServiceException ex = new ServiceException(UNKNOWN_SERVICE_MSG);

        ResponseEntity<ErrorResponse> result = errorHandler.handleServiceException(ex);

        assertNotNull(result.getBody());
        assertEquals(INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals(SERVER_ERROR.getCode(), result.getBody().getErrorCode());
        assertEquals(SERVER_ERROR.getMessage(), result.getBody().getErrorMessage());
    }

    @Test
    void handleDaoException_whenThrown_returnsDatabaseError() {
        DaoException ex = new DaoException(DAO_ERROR_MSG);

        ResponseEntity<ErrorResponse> result = errorHandler.handleDaoException(ex);

        assertNotNull(result.getBody());
        assertEquals(INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals(DATABASE_ERROR.getCode(), result.getBody().getErrorCode());
        assertEquals(DATABASE_ERROR.getMessage(), result.getBody().getErrorMessage());
    }

    @Test
    void handleEntityNotFoundException_whenThrown_returnsNotFoundErrorWithMessage() {
        EntityNotFoundException ex = new EntityNotFoundException(ENTITY_NOT_FOUND_MSG);
        String expectedMessage = format(NOT_FOUND_ERROR.getMessage() + ENTITY_NOT_FOUND_MSG);

        ResponseEntity<ErrorResponse> result = errorHandler.handleEntityNotFoundException(ex);

        assertNotNull(result.getBody());
        assertEquals(NOT_FOUND, result.getStatusCode());
        assertEquals(NOT_FOUND_ERROR.getCode(), result.getBody().getErrorCode());
        assertEquals(expectedMessage, result.getBody().getErrorMessage());
    }

    @Test
    void handleValidationException_whenThrown_returnsValidationErrorWithMessage() {
        ConstraintViolationException ex = new ConstraintViolationException(VALIDATION_MSG, null);
        String expectedMessage = format(VALIDATION_ERROR.getMessage() + VALIDATION_MSG);

        ResponseEntity<ErrorResponse> result = errorHandler.handleValidationException(ex);

        assertNotNull(result.getBody());
        assertEquals(BAD_REQUEST, result.getStatusCode());
        assertEquals(VALIDATION_ERROR.getCode(), result.getBody().getErrorCode());
        assertEquals(expectedMessage, result.getBody().getErrorMessage());
    }

    @Test
    void handleUserNotAuthenticatedException_whenThrown_returnsAuthenticationError() {
        UserNotAuthenticatedException ex = new UserNotAuthenticatedException(AUTH_ERROR_MSG);

        ResponseEntity<ErrorResponse> result = errorHandler.handleUserNotAuthenticatedException(ex);

        assertNotNull(result.getBody());
        assertEquals(UNAUTHORIZED, result.getStatusCode());
        assertEquals(AUTHENTICATION_ERROR.getCode(), result.getBody().getErrorCode());
        assertEquals(AUTHENTICATION_ERROR.getMessage(), result.getBody().getErrorMessage());
    }

    @Test
    void handleUnhandledException_whenThrown_returnsServerError() {
        RuntimeException ex = new RuntimeException(UNHANDLED_ERROR_MSG);

        ResponseEntity<ErrorResponse> result = errorHandler.handleUnhandledException(ex);

        assertNotNull(result.getBody());
        assertEquals(INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals(SERVER_ERROR.getCode(), result.getBody().getErrorCode());
        assertEquals(SERVER_ERROR.getMessage(), result.getBody().getErrorMessage());
    }

    private static Stream<Arguments> invalidRequestProvider() {
        return Stream.of("Invalid trainee username",
                        "Invalid trainer username",
                        "Trainee username must be provided",
                        "Trainer username must be provided",
                        "Username must not be null",
                        "Invalid training type")
                .map(Arguments::of);
    }
}