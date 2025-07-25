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
import static org.apache.commons.lang3.StringUtils.isNotBlank;

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

        return apiError == INVALID_REQUEST_ERROR
                ? buildErrorResponse(apiError, ex.getMessage())
                : buildErrorResponse(apiError);
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

    private ResponseEntity<ErrorResponse> buildErrorResponse(ApiError apiError) {
        return buildErrorResponse(apiError, null);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(ApiError apiError, String details) {
        String baseMessage = apiError.getMessage();
        String errorDetails = isNotBlank(details) ? details : "";

        ErrorResponse errorResponse = new ErrorResponse(apiError.getCode(), baseMessage + errorDetails);

        return new ResponseEntity<>(errorResponse, apiError.getHttpStatus());
    }

    private ApiError resolveError(ServiceException ex) {
        String message = ex.getMessage() != null ? ex.getMessage() : "";

        return BAD_REQUEST_PREFIXES.stream()
                .filter(prefix -> message.toLowerCase().startsWith(prefix.toLowerCase()))
                .findFirst()
                .map(x -> INVALID_REQUEST_ERROR)
                .orElse(SERVER_ERROR);
    }
}