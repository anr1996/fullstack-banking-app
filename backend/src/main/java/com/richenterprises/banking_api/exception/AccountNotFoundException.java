package com.richenterprises.banking_api.exception;

/**
 * Thrown when a requested account does not exist in the database.
 * This maps to HTTP 404 Not Found.
 */
public class AccountNotFoundException extends RuntimeException {

    /**
     * Constructs the exception with a detail message.
     * @param message (The reason the account was not found.)
     */
    public AccountNotFoundException(String message) {
        super(message);
    }
    
}
