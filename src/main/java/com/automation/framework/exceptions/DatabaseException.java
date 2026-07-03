package com.automation.framework.exceptions;

/**
 * Thrown when a database connection or query operation fails.
 */
public class DatabaseException extends FrameworkException {

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
