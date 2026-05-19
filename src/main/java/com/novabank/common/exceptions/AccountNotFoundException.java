package com.novabank.common.exceptions;

/**
 * Exception thrown when a bank account cannot be found.
 * Maps to HTTP 404 Not Found status.
 *
 * WHY: Distinguishes account lookup failures from other resource not found scenarios,
 * enabling precise error handling and logging for auditing purposes.
 */
public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String message) {
        super(message);
    }

    public AccountNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

