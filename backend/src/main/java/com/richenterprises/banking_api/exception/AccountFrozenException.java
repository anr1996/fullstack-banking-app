package com.richenterprises.banking_api.exception;

/**
 * Thrown when an operation is attempted on an account whose status is FROZEN.
 * This maps to HTTP 409 Conflict, because the current state of the resource prevents the requested
 * operation from being processed.
 */
public class AccountFrozenException extends RuntimeException {
    /**
     * Constructs the exception with a detail message.
     * 
     * @param message (The reason the account is frozen and the operation was rejected.)
     */
    public AccountFrozenException(String message) {
        super(message);
    }
}
