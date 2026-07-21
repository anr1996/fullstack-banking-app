package com.richenterprises.banking_api.config;

import com.richenterprises.banking_api.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.
       configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * This will configure Spring Security for the banking API. 
 * It enables the JWT authentication, stateless sessions, and role-based method security.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 
     * The constructor injection of the JWT filter.
     */
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * 
     * @param http (http the HttpSecurity builder.)
     * @return (Returns the configured SecurityFilterChain.)
     * @throws Exception (Throws an exception if configuration fails.)
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
        .requestMatchers("/auth/**", "/health").permitAll().anyRequest().authenticated())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    
       return http.build();
    }

    /**
     * This will provide the password encoder for hashing and verifying passwords.
     * @return (Returns the BCryptPasswordEncoder instance.)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * This will expose the AuthenticationManager for manual authentication if needed.
     * @param config (configures the authentication configuration.)
     * @return (Returns the AuthenticationManager.)
     * @throws Exception (Throws exception if configuration fails.)
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws
    Exception {
        return config.getAuthenticationManager();
    }
}

