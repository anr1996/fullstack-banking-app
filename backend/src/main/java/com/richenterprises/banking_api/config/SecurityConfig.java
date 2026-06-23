package com.richenterprises.banking_api.config;

import com.richenterprises.banking_api.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration
       .AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * The central security configuration.
 * This defines password encoding, authentication rules, and the security filter chain.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * The constructor injection of the JWT filter. 
     * Spring will provide the filter bean from the application context.
     */
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * The Password encoder bean. 
     * This uses BCrypt, which will automatically salt and hash passwords. 
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * The AuthenticationManager bean. 
     * This is required by Spring Security's authentication infrastructure. 
     * It is used internally by the auth service when we implement custom authentication. 
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws
    Exception { return config.getAuthenticationManager(); }


    /**
     * 
     * @param http (The HttpSecurity builder which is provided by Spring security.)
     * @return (The configured SecurityFilterChain is returned.)
     * @throws Exception (Exception is thrown if the security configuration fails to build.)
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws
    Exception {
        http
        
        // This will disable CSRF because it is a stateless REST API, not a form-based web app.
        .csrf(AbstractHttpConfigurer::disable) 

        // This Defines the authorization rules
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/**").permitAll() // The public endpoints.
            .anyRequest().authenticated() // Everything else will require authentication.
        )

        // This sets the session management to STATELESS where there are no server-side sessions.
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )

        // This will add the JWT filter before the standard username/password filter. 
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    
}
