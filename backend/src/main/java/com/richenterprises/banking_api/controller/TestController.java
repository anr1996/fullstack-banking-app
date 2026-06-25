package com.richenterprises.banking_api.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * The test controller for verifying authentication.
 * The protected endpoint which requires a valid JWT token;
 */
@RestController
@RequestMapping("/api")
public class TestController {

    /**
     * This returns the currently authenticated user's email.
     * The JWT filter extracts this from the token and stores it in the SecurityContext.
     * 
     * @return (returns a JSON map with the user's email.)
     */
    @GetMapping("/me")
    public Map<String, String> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext()
                                                             .getAuthentication();
        String email = authentication.getName();
        return Map.of("email", email);
    }
}
