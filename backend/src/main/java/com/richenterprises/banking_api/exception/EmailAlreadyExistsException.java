package com.richenterprises.banking_api.exception;

/**
 * This is thrown when a registration is attempted with an email that already exists.
 * It maps to HTTP 409 Conflict.
 */
public class EmailAlreadyExistsException  extends RuntimeException{

    /**
     * This constructs the exception with a message describing the conflict.
     * 
     * @param message (The detail message.)
     */
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
    
}
