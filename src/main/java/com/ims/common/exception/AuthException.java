package com.ims.common.exception;

/**
 * Thrown when authentication or authorisation fails (bad credentials,
 * deactivated account, or insufficient role).
 */
public class AuthException extends Exception {

    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
