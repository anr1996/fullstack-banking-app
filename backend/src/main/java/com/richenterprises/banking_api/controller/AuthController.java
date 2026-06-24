package com.richenterprises.banking_api.controller;

import com.richenterprises.banking_api.dto.AuthResponse;
import com.richenterprises.banking_api.dto.LoginRequest;
import com.richenterprises.banking_api.dto.RegisterRequest;
import com.richenterprises.banking_api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The REST controller for authentication endpoints.
 * This will expose the public endpoints for user registration and logic.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * The constructor injection of AuthService
     */
    public AuthController(AuthService authService) {
        this.authService = authService;       
    }

    /**
     * This will register a new customer account. 
     * 
     * @param request (Requests the registration payload.)
     * @return (Returns 201 created with the JWT token.) 
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody
    RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * This will authenticate a user and return a JWT token.
     * 
     * @param request (Requests the login payload.)
     * @return (Returns 200 OK with the JWT token.)
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
}
