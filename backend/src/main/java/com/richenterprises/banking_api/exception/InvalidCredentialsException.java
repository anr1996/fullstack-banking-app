package com.richenterprises.banking_api.exception;

/**
 * This is thrown when a login is attempted with an email or password that does not match.
 * It maps to HTTP 401 unauthorized.
 */
public class InvalidCredentialsException extends RuntimeException {
    
    /**
     * This constructs the exception with a message describing the failure.
     * 
     * @param message (The detail message.)
     */
    public InvalidCredentialsException(String message) {
        super(message);
    }
}

