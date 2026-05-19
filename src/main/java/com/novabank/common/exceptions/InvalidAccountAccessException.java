package com.novabank.common.exceptions;

/**
 * Exception thrown when a user attempts to access an account they don't own.
 * Maps to HTTP 403 Forbidden status.
 *
 * WHY: Distinguishes unauthorized access (user not authenticated) from forbidden access
 * (user authenticated but lacks permission). Critical for security audit logs and
 * detecting potential fraud attempts.
 */
public class InvalidAccountAccessException extends RuntimeException {
    public InvalidAccountAccessException(String message) {
        super(message);
    }

    public InvalidAccountAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}

