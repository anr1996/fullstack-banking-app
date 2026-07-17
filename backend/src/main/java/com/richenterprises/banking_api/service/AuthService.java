package com.richenterprises.banking_api.service;

import com.richenterprises.banking_api.dto.AuthResponse;
import com.richenterprises.banking_api.dto.LoginRequest;
import com.richenterprises.banking_api.dto.RegisterRequest;
import com.richenterprises.banking_api.entity.Account;
import com.richenterprises.banking_api.entity.AccountType;
import com.richenterprises.banking_api.entity.Role;
import com.richenterprises.banking_api.entity.User;
import com.richenterprises.banking_api.repository.AccountRepository;
import com.richenterprises.banking_api.repository.UserRepository;
import com.richenterprises.banking_api.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * This is the authentication service. 
 * It handles user registration and login business logic.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    /**
     * The constructor injection of all dependencies. 
     * Spring will provide these beans from the application context.
     */
    public AuthService(UserRepository userRepository,
                       AccountRepository accountRepository,
                       PasswordEncoder passwordEncoder, 
                       JwtUtil jwtUtil) {
                        this.userRepository = userRepository;
                        this.accountRepository = accountRepository;
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

        // step 4: Create a default CHECKING account for the new user.
        // Every bank customer needs at least one account to use the services.
        Account checkingAccount = Account.builder()
                                  .user(user)
                                  .accountNumber(generateAccountNumber())
                                  .type(AccountType.CHECKING)
                                  .balance(0L) // $0.00 The starting balance.
                .status(com.richenterprises.banking_api.entity.AccountStatus.ACTIVE)
                .build();
        
        accountRepository.save(checkingAccount);

        // Step 5 Generate the JWT token.
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

    /** 
     * This will generate a unique account number.
     * Format: ACC- + first 8 characters of a UUID
     * 
     * @return (Returns a unique account number string.)
     */
    private String generateAccountNumber() {
        return "ACC-" + UUID.randomUUID().toString().substring(0,8).toUpperCase();
    }
}
