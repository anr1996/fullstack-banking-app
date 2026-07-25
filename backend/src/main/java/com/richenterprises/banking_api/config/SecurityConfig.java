package com.richenterprises.banking_api.config;

import com.richenterprises.banking_api.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
/**
 * This will configure Spring Security for the banking API. 
 * It enables the JWT authentication, stateless sessions, and role-based method security.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     *The BCrypt cost factor.
     Each increment doubles the hashing time, which slows offline brute force attacks against
     a stolen password database.
     */
    private static final int BCRYPT_STRENGTH = 12;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * The browser origins permitted to call this API.
     * The local development uses Vite proxy, so this is only exercised when the fronted is
     * served from a different origin than the API.
     */
    private final List<String> allowedOrigins;

    /**
     * 
     * The constructor injection of the JWT filter and CORS settings.
     * @param jwtAuthenticationFilter (The filter that authenticates Bearer tokens.)
     * @param allowedOrigins (The comma separated list of permitted browser origins.)
     */
    public SecurityConfig(
        JwtAuthenticationFilter jwtAuthenticationFilter,
        @Value("${app.cors.allowed-origins:http://localhost:5173}")  List<String> allowedOrigins) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.allowedOrigins = allowedOrigins;
    }

    /**
     * 
     * @param http (http the HttpSecurity builder.)
     * @return (Returns the configured SecurityFilterChain.)
     * @throws Exception (Throws an exception if configuration fails.)
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                /**
                 * CSRF protection guards against a browser silently attaching ambient credientials
                 * such as cookies. This API carries its credential in an Authorization header that 
                 * an attacker's page cannot set, so that the attack CSRF prevents is not reachable
                 * here. This must be revisited if the token ever moves into a cooke.
                 */
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .sessionManagement
                (session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**","/health")
                .permitAll().anyRequest().authenticated())

                /**
                 * Without these handlers Spring Security will answer both 
                 * "you are not authenticated" and "you are authenticated but not allowed" with 403.
                 * Separating them lets the client log an expired session out on 401 while leaving 
                 * a permission failure on 403 alone.
                 */
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> 
                    writeError(response, HttpServletResponse.SC_UNAUTHORIZED, 
                        "Authentication is required to access this resource."))
                .accessDeniedHandler((request, response, accessDeniedException) -> 
                    writeError(response, HttpServletResponse.SC_FORBIDDEN,
                        "You do not have permission to access this resource.")))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
            
    
       return http.build();
    }


    /**
     * This writes a minimal JSON error body so the client receives a consistent shape instead of
     * an HTML error page.
     * 
     * @param response (The HTTP response to write to.)
     * @param status (The HTTP status code to send.)
     * @param message (A generic message that does not reveal why the check failed.)
     */
    private void writeError(HttpServletResponse response, int status, String message)
        throws java.io.IOException {
            response.setStatus(status);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"status\":" + status + ",\"message\":\"" + message + "\"}");
        }

    /**
     * This defines which browser origins, methods, and headers are permitted.
     * 
     * @return (Return the CORS configuration source used by the filter chain.)
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // setAllowedOrigins requires exact origins. The wildcards are rejected whenever
        // credentials are allowed, which is the behavior we want.
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * This will provide the password encoder for hashing and verifying passwords.
     * 
     * @return (Returns the BCryptPasswordEncoder instance.)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(BCRYPT_STRENGTH);
    }
}

