package com.richenterprises.banking_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The global exception handler.
 * It converts exceptions thrown anywhere in the application into consistent JSON error responses
 * with the correct HTTP status code, so controllers and services never build error bodies 
 * themselves.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * This builds a consistent error body.
     * 
     * @param status (The HTTP status to report.)
     * @param message (A client safe description of the error.)
     * @return (Returns the response entity carrying the error body.)
     */
    private ResponseEntity<Map<String, Object>> build(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }

    /**
     * This handles a registration conflict.
     * 
     * @param ex (The thrown exception.)
     * @return (Returns 409 Conflict.)
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleEmailExists(EmailAlreadyExistsException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * This handles a failed login.
     * @param ex (The thrown exception.)
     * @return (Returns 401 Unauthorized.)
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials(InvalidCredentialsException ex) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    /**
     * This handles a request body that fails Bean Validation.
     * It reports the first field error so the client sees which field was wrong without exposing
     * the full internal validation structure.
     * 
     * @param ex (The validation exception raised by @Valid.)
     * @return (Returns 400 Bad Request.)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Validation failed.");
        return build(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * This handles a missing account.
     * It returns 404 whether the account does not exist or the caller does not own it.
     * So an attacker cannot enumerate account IDs by probing different numbers.
     * 
     * @param ex (The thrown exception.)
     * @return (Returns 404 Not Found.)
     */
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleAccountNotFound(AccountNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());    
    }

    /**
     * This handles insufficient funds during a withdrawl or transfer.
     * 422 Unprocessable Entity: syntactically valid but violates a business rule.
     * 
     * @param ex (The thrown exception.)
     * @return (Returns 422 Unprocessable Entity.)
     */
    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientFunds(InsufficientFundsException ex) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    /**
     * This handles operations attempted on a frozen account.
     * 409 Conflict: the current state of the resource prevents the operation.
     * 
     * @param ex (The thrown exception.)
     * @return (Returns 409 Conflict.)
     */
    @ExceptionHandler(AccountFrozenException.class)
    public ResponseEntity<Map<String, Object>> handleAccountFrozen(AccountFrozenException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * This is the catch all for any unhandled exception.
     * It returns a generic message so an unexpected error never leaks a stack trace
     * or internal detail to the client. The real cause should be logged.
     * 
     * @param ex (The unhandled exception.)
     * @return (Returns 500 Internal Server Error.)
     */
    @ExceptionHandler(Exception.class) 
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception ex) {
        // In a later phase, log ex here with a proper logger.
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
    }
}
