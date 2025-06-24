package com.gcs.app.exception;

public class StorageInitializationException extends RuntimeException {
    public StorageInitializationException(String message) {
        super(message);
    }

    public StorageInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public StorageInitializationException(String entityType, int lineNumber) {
        super(String.format("Invalid %s format at line %d", entityType, lineNumber));
    }

    public StorageInitializationException(String entityType, int lineNumber, String details) {
        super(String.format("Invalid %s format at line %d: %s", entityType, lineNumber, details));
    }
}