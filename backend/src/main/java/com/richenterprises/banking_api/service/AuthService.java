package com.richenterprises.banking_api.service;

import com.richenterprises.banking_api.dto.AuthResponse;
import com.richenterprises.banking_api.dto.LoginRequest;
import com.richenterprises.banking_api.dto.RegisterRequest;
import com.richenterprises.banking_api.entity.Role;
import com.richenterprises.banking_api.entity.User;
import com.richenterprises.banking_api.repository.UserRepository;
import com.richenterprises.banking_api.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * This is the authentication service. 
 * It handles user registration and login business logic.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    /**
     * The constructor injection of all dependencies. 
     * Spring will provide these beans from the application context.
     */
    public AuthService(UserRepository userRepository, 
                       PasswordEncoder passwordEncoder, 
                       JwtUtil jwtUtil) {
                        this.userRepository = userRepository;
                        this.passwordEncoder = passwordEncoder;
                        this.jwtUtil = jwtUtil;
    }
    
    /**
     * This will register a new user. 
     * @param request (Request the registration payload.)
     * @return (Returns an AuthResponse containing the JWT token.)
     * @throws RuntimeException (Throws an exception of the email is already registered.)
     */
    public AuthResponse register(RegisterRequest request) {
        // Step 1: Check for duplicate email.
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("The email is already registered.");
        }

        // Step 2: Hash the password.
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Step 3: Create and save the user.
        User user = User.builder()
                        .email(request.getEmail())
                        .passwordHash(hashedPassword)
                        .name(request.getName())
                        .role(Role.CUSTOMER) // The default role for self-registration.
                        .build();

        userRepository.save(user);

        // Step 4 Generate the JWT token.
        String token = jwtUtil.generateToken(user.getEmail(), 
                                             user.getRole().name());

        return AuthResponse.builder()
                           .token(token)
                           .build();
            }
        
    /**
     * This will authenticate a user and return a JWT token. 
     * 
     * @param request (Requests the login payload.)
     * @return (Returns an AuthResponse containing the JWT token.)
     * @throws RuntimeException (Throws an exception if the credentials are invalid.)
     */
    public AuthResponse login(LoginRequest request) {
        // Step 1 Find the user by email.
        User user = userRepository
             .findByEmail(request.getEmail())
             .orElseThrow(() -> new RuntimeException("Invalid credentials."));

        // Step 2: Verify the password.
        if (!passwordEncoder.matches(
            request.getPassword(),
            user.getPasswordHash())) {
                throw new RuntimeException("Invalid credentials.");
        }

        // Step 3: Generate the Jwt token
        String token = jwtUtil.generateToken(
            user.getEmail(), 
            user.getRole().name());

        return AuthResponse.builder()
                            .token(token)
                            .build();
                                      
    }
}
