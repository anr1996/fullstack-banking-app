package com.richenterprises.banking_api.service;

import com.richenterprises.banking_api.dto.AuthResponse;
import com.richenterprises.banking_api.dto.LoginRequest;
import com.richenterprises.banking_api.dto.RegisterRequest;
import com.richenterprises.banking_api.entity.Account;
import com.richenterprises.banking_api.entity.AccountStatus;
import com.richenterprises.banking_api.entity.AccountType;
import com.richenterprises.banking_api.entity.Role;
import com.richenterprises.banking_api.entity.User;
import com.richenterprises.banking_api.exception.EmailAlreadyExistsException;
import com.richenterprises.banking_api.exception.InvalidCredentialsException;
import com.richenterprises.banking_api.repository.AccountRepository;
import com.richenterprises.banking_api.repository.UserRepository;
import com.richenterprises.banking_api.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * @param userRepository (The repository for user records.)
     * @param accountRepository (The repository for account records.)
     * @param passwordEncoder (The encoder used to hash and verify passwords.)
     * @param jwtUtil (The utility that issues JWT tokens.)
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
     * This will register a new user and open their first account.
     * Both writes happen in one transaction, so a failure partway through leaves no half-created
     * user without an account.
     * @param request (The registration payload.)
     * @return (Returns an AuthResponse containing the JWT token.)
     * @throws EmailAlreadyExistsException (Throws if the email is already registered.)
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("The email is already registered");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Create and save the user.
        User user = User.builder()
                        .email(request.getEmail())
                        .passwordHash(hashedPassword)
                        .name(request.getName())
                        .role(Role.CUSTOMER) // The default role for self-registration.
                        .build();

        userRepository.save(user);

        // Every bank customer needs at least one account, opened with a zero balance.
        Account checkingAccount = Account.builder()
                .user(user)
                .accountNumber(generateAccountNumber())
                .type(AccountType.CHECKING)
                .balance(0L) // $0.00 The starting balance.
                .status(AccountStatus.ACTIVE)
                .build();
        
        accountRepository.save(checkingAccount);

        // Generate the JWT token.
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .build();
            }
        
    /**
     * This will authenticate a user and return a JWT token. 
     * The same error is returned whether the email is unknown or the password is wrong, so an 
     * attacker cannot tell which accounts exist.
     * 
     * @param request (The login payload.)
     * @return (Returns an AuthResponse containing the JWT token.)
     * @throws InvalidCredentialsException (Throws if the email or password is wrong.)
     */
    public AuthResponse login(LoginRequest request) {
        User user = userRepository
             .findByEmail(request.getEmail())
             .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                throw new InvalidCredentialsException("Invalid email or password.");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .build();                          
    }

    /** 
     * This will generate a unique account number.
     * The format is ACC- followed by the first eight characters of a UUID.
     * 
     * @return (Returns a unique account number string.)
     */
    private String generateAccountNumber() {
        return "ACC-" + UUID.randomUUID().toString().substring(0,8).toUpperCase();
    }
}
