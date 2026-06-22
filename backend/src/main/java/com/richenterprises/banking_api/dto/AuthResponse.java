package com.richenterprises.banking_api.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


/**
 * The response is returned after a successful login.
 * This containts the JWT token the client must send in the authorization header
 * for each subsequent request.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder


public class AuthResponse {

    /**
     * The JWT access token.
     * The client will store this and then send it as: Authorization: Bearer <token>
     */
    private String token;
 
}
