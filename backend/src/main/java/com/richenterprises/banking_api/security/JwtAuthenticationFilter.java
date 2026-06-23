package com.richenterprises.banking_api.security;

import com.richenterprises.banking_api.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import
org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
/**

 * The JWT authentication filter. 
 * Intercepts every HTTP request, validates the Bearer token,
 * and sets the authenticated user in the SecurityContext.
 */

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{
    private final JwtUtil jwtUtil;

    /**
     * The constructor injection of JwtUtil. 
     * Spring will automatically provide the JwtUtil bean from the application context.
     */
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * This is the core filter logic which runs once per request. 
     * @param request (The HTTP request.)
     * @param response (The HTTP response.)
     * @param filterChain (The chain of remaing filters to execute.)
     */

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Step 1: The authorization header is extracted.
        String authHeader = request.getHeader("Authorization");

        // Step 2: Check if the header exists and starts with "Bearer ". 
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            // No token is present. Therefore, the request will continue unauthenticated.
            // The next filter will handle access control
            filterChain.doFilter(request, response);
            return;
        }

        // Step 3: The (remove "Bearer" prefix) token string is extracted. 
        String token = authHeader.substring(7);

        // Step 4: Validate the token
        if (!jwtUtil.validateToken(token)) {
            // If the token is invalid or expired then do not authenticate.
            filterChain.doFilter(request, response);
            return;
        }

        // Step 5: The user identity will be extracted from the token.
        String email = jwtUtil.extractEmail(token);
        String role = jwtUtil.extractRole(token);

        // Step 6: Build a UserDetails object which is Spring security's 
        // representation of a user.
        UserDetails userDetails = User.builder()
                                      .username(email)
                                      .password("")
                                      .roles(role)
                                      .build();
        
        // Step 7: An authentication token will be created.
        // The third parameter (authorities) is empty because the roles are set on UserDetails.
        UsernamePasswordAuthenticationToken authentication = 
        new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );

        // Step 8: Store the authentication in the SecurityContext.
        // This will make the user's "Logged in" for the duration of this request.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Step 9: This will pass the request to the next filter in the chain.
        filterChain.doFilter(request, response);
    }

    
}
