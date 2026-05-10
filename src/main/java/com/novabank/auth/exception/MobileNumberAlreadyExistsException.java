package com.novabank.auth.exception;

public class MobileNumberAlreadyExistsException extends RuntimeException {

    public MobileNumberAlreadyExistsException(String message) {
        super(message);
    }
}
