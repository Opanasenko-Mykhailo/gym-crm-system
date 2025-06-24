package com.gcs.app.exception;

public class EntityNotFoundException extends DaoException {
    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityNotFoundException(Long id) {
        super("Entity with ID " + id + " not found.");
    }

    public EntityNotFoundException(String entityName, Long id) {
        super(entityName + " with ID " + id + " not found.");
    }
}