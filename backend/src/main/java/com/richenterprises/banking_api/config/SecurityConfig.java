package com.richenterprises.banking_api.config;

// @configuration will tell Spring: "The class containts bean definitions"
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// PasswordEncoder is the interface used; BCryptPasswordEncode is the implementation
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * The security configuration class.
 * This exposes beans needed by the authentication layer
 */

@Configuration
public class SecurityConfig {
    
    /**
     * This creates a PasswordEncoder bean that uses BCrypt.
     * Spring will store this bean in the "application context" (a container of objects).
     * Other classes can request the bean via constructor injection.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
