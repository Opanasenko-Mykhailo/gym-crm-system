package com.gcs.app.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static com.gcs.app.exception.ApiError.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(MockitoExtension.class)
class ErrorHandlerTest {

    private static final String VALIDATION_MSG = "Username must not be blank";
    private static final String ENTITY_NOT_FOUND_MSG = "Trainee with ID 10 was not found in the database";
    private static final String INVALID_REQUEST_MSG = "Invalid trainee username format: must contain only letters";
    private static final String UNKNOWN_SERVICE_MSG = "Unexpected error occurred during trainee registration";
    private static final String DAO_ERROR_MSG = "Failed to execute database query for trainee entity";
    private static final String AUTH_ERROR_MSG = "Authentication token is missing or invalid";
    private static final String UNHANDLED_ERROR_MSG = "Unhandled exception occurred while processing request";

    @InjectMocks
    private ErrorHandler errorHandler;

    @Test
    void handleServiceException_whenPrefixMatches_returnsInvalidRequestError() {
        ServiceException ex = new ServiceException(INVALID_REQUEST_MSG);

        ResponseEntity<ErrorResponse> result = errorHandler.handleServiceException(ex);

        assertNotNull(result.getBody());
        assertEquals(BAD_REQUEST, result.getStatusCode());
        assertEquals(String.valueOf(INVALID_REQUEST_ERROR.getCode()), result.getBody().errorCode());
        assertTrue(result.getBody().errorMessage().contains(INVALID_REQUEST_ERROR.getMessage()));
    }

    @Test
    void handleServiceException_whenPrefixUnknown_returnsServerError() {
        ServiceException ex = new ServiceException(UNKNOWN_SERVICE_MSG);

        ResponseEntity<ErrorResponse> result = errorHandler.handleServiceException(ex);

        assertNotNull(result.getBody());
        assertEquals(INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals(String.valueOf(SERVER_ERROR.getCode()), result.getBody().errorCode());
        assertEquals(SERVER_ERROR.getMessage(), result.getBody().errorMessage());
    }

    @Test
    void handleDaoException_whenThrown_returnsDatabaseError() {
        DaoException ex = new DaoException(DAO_ERROR_MSG);

        ResponseEntity<ErrorResponse> result = errorHandler.handleDaoException(ex);

        assertNotNull(result.getBody());
        assertEquals(INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals(String.valueOf(DATABASE_ERROR.getCode()), result.getBody().errorCode());
        assertEquals(DATABASE_ERROR.getMessage(), result.getBody().errorMessage());
    }

    @Test
    void handleEntityNotFoundException_whenThrown_returnsNotFoundErrorWithMessage() {
        EntityNotFoundException ex = new EntityNotFoundException(ENTITY_NOT_FOUND_MSG);

        ResponseEntity<ErrorResponse> result = errorHandler.handleEntityNotFoundException(ex);

        assertNotNull(result.getBody());
        assertEquals(NOT_FOUND, result.getStatusCode());
        assertEquals(String.valueOf(NOT_FOUND_ERROR.getCode()), result.getBody().errorCode());
        assertTrue(result.getBody().errorMessage().contains(NOT_FOUND_ERROR.getMessage()));
        assertTrue(result.getBody().errorMessage().contains(ENTITY_NOT_FOUND_MSG));
    }

    @Test
    void handleValidationException_whenThrown_returnsValidationErrorWithMessage() {
        ConstraintViolationException ex = new ConstraintViolationException(VALIDATION_MSG, null);

        ResponseEntity<ErrorResponse> result = errorHandler.handleValidationException(ex);

        assertNotNull(result.getBody());
        assertEquals(BAD_REQUEST, result.getStatusCode());
        assertEquals(String.valueOf(VALIDATION_ERROR.getCode()), result.getBody().errorCode());
        assertTrue(result.getBody().errorMessage().contains(VALIDATION_ERROR.getMessage()));
        assertTrue(result.getBody().errorMessage().contains(VALIDATION_MSG));
    }

    @Test
    void handleUserNotAuthenticatedException_whenThrown_returnsAuthenticationError() {
        UserNotAuthenticatedException ex = new UserNotAuthenticatedException(AUTH_ERROR_MSG);

        ResponseEntity<ErrorResponse> result = errorHandler.handleUserNotAuthenticatedException(ex);

        assertNotNull(result.getBody());
        assertEquals(UNAUTHORIZED, result.getStatusCode());
        assertEquals(String.valueOf(AUTHENTICATION_ERROR.getCode()), result.getBody().errorCode());
        assertEquals(AUTHENTICATION_ERROR.getMessage(), result.getBody().errorMessage());
    }

    @Test
    void handleUnhandledException_whenThrown_returnsServerError() {
        RuntimeException ex = new RuntimeException(UNHANDLED_ERROR_MSG);

        ResponseEntity<ErrorResponse> result = errorHandler.handleUnhandledException(ex);

        assertNotNull(result.getBody());
        assertEquals(INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals(String.valueOf(SERVER_ERROR.getCode()), result.getBody().errorCode());
        assertEquals(SERVER_ERROR.getMessage(), result.getBody().errorMessage());
    }
}