package com.automation.framework.exceptions;

/**
 * Thrown when a REST/SOAP API interaction fails or returns an unexpected result.
 */
public class ApiException extends FrameworkException {

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
