package com.novabank.common.exceptions;

/**
 * Exception thrown when a user has valid credentials but lacks permission for an operation.
 * Maps to HTTP 403 Forbidden status.
 *
 * WHY: Distinguishes authentication failure (401) from authorization failure (403).
 * Important for security logging and proper HTTP status code semantics.
 */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}

