package com.automation.framework.exceptions;

/**
 * Base unchecked exception for all framework-level failures.
 * Wrapping lower-level checked exceptions in this type keeps test code clean
 * and makes framework failures easy to distinguish from test assertion failures.
 */
public class FrameworkException extends RuntimeException {

    public FrameworkException(String message) {
        super(message);
    }

    public FrameworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
