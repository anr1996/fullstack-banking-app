package com.richenterprises.banking_api.exception;

/**
 * Thrown when a transfer request carries an idempotency key that has already been processed.
 * This maps to HTTP 409 Conflict, because the resource state already reflects this exact operation
 * and replaying it would produce a duplicate money movement.
 */
public class DuplicateTransferException extends RuntimeException {
    /**
     * Constructs the exception with a detail message.
     * 
     * @param message (The reason the transfer was rejected as a duplicate.)
     */
    public DuplicateTransferException(String message) {
        super(message);
    }
}
