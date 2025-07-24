package com.gcs.app.exception;

import com.gcs.app.rest.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Set;

import static com.gcs.app.exception.ApiError.AUTHENTICATION_ERROR;
import static com.gcs.app.exception.ApiError.DATABASE_ERROR;
import static com.gcs.app.exception.ApiError.INVALID_REQUEST_ERROR;
import static com.gcs.app.exception.ApiError.NOT_FOUND_ERROR;
import static com.gcs.app.exception.ApiError.SERVER_ERROR;
import static com.gcs.app.exception.ApiError.VALIDATION_ERROR;

@ControllerAdvice
@Slf4j
public class ErrorHandler {

    private static final Set<String> BAD_REQUEST_PREFIXES = Set.of(
            "Invalid trainee username",
            "Invalid trainer username",
            "Trainee username must be provided",
            "Trainer username must be provided",
            "Username must not be null",
            "Invalid training type"
    );

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleServiceException(ServiceException ex) {
        log.error("ServiceException: {}", ex.getMessage(), ex);
        ApiError apiError = resolveError(ex);

        return buildErrorResponse(apiError, ex.getMessage());
    }

    @ExceptionHandler(DaoException.class)
    public ResponseEntity<ErrorResponse> handleDaoException(DaoException ex) {
        log.error("Database Exception: {}", ex.getMessage(), ex);

        return buildErrorResponse(DATABASE_ERROR);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error("EntityNotFoundException: {}", ex.getMessage(), ex);

        return buildErrorResponse(NOT_FOUND_ERROR, ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ConstraintViolationException ex) {
        log.error("ConstraintViolationException: {}", ex.getMessage(), ex);

        return buildErrorResponse(VALIDATION_ERROR, ex.getMessage());
    }

    @ExceptionHandler(UserNotAuthenticatedException.class)
    public ResponseEntity<ErrorResponse> handleUserNotAuthenticatedException(UserNotAuthenticatedException ex) {
        log.error("UserNotAuthenticatedException: {}", ex.getMessage(), ex);

        return buildErrorResponse(AUTHENTICATION_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleUnhandledException(RuntimeException ex) {
        log.error("Unhandled RuntimeException: {}", ex.getMessage(), ex);

        return buildErrorResponse(SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(ApiError apiError, String details) {
        String baseMessage = apiError.getMessage();

        ErrorResponse errorResponse = new ErrorResponse(apiError.getCode(), baseMessage);

        return new ResponseEntity<>(errorResponse, apiError.getHttpStatus());
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(ApiError apiError) {
        return buildErrorResponse(apiError, null);
    }

    private ApiError resolveError(ServiceException ex) {
        String message = ex.getMessage() != null ? ex.getMessage() : "";
        boolean isBadRequest = BAD_REQUEST_PREFIXES.stream()
                .anyMatch(prefix -> message.toLowerCase().startsWith(prefix.toLowerCase()));

        return isBadRequest ? INVALID_REQUEST_ERROR : SERVER_ERROR;
    }
}