package com.ims.common.exception;

/**
 * Thrown when user-supplied input fails business-rule validation.
 * The message is always safe to show directly in the UI.
 */
public class ValidationException extends Exception {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
