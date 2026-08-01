package com.richenterprises.banking_api.exception;

/**
 * Thrown when an account does not have enough balance to complete a withdrawal or transfer.
 * This maps to HTTP 422 Unprocessable Entity, because the request is syntactically valid but 
 * violates a business rule (the account invariant that balance cannot go negative.)
 */
public class InsufficientFundsException extends RuntimeException {

    /**
     * Constructs the exception with a detail message.
     * 
     * @param message (The reason the funds are insufficient.)
     */
    public InsufficientFundsException(String message) {
        super(message);
    }
}