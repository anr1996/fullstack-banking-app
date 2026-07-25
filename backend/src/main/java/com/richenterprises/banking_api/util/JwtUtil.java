package com.richenterprises.banking_api.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * The utility class to implement the JWT token creation, extraction, and validation.
 * Stateless (no database lookup).
 * The token itself will carry the user's identity.
*/
@Component
public class JwtUtil {

    /**
     * The minimum secret length in bytes.
     * HS256 requires a key of at least 256 bits, which is 32 bytes.
     */
    private static final int MIN_SECRET_BYTES = 32;

    /**
     * The signing key, derived once from the configured secret.
     * Deriving it a single time avoids repeating the work on every token operation.
     */
    private final SecretKey signingKey;

    /**
     * The token lifetime in milliseconds.
     * It is injected from application.properties. The default is fifteen minutes.
     */
    private final long expiration;

    /**
     * The constructor injection of the JWT settings.
     * It validates the secret length at startup so a misconfiguration fails immediately
     * rather than at the first login attempt.
     * 
     * @param secret (The signing secret, injected from configuration.)
     * @param expiration (The token lifetime in milliseconds.) 
     */
    public JwtUtil(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.expiration:900000}") long expiration) {
            byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            if (keyBytes.length < MIN_SECRET_BYTES) {
                throw new IllegalStateException(
                    "jwt.secret must be at least " + MIN_SECRET_BYTES + " bytes for HS256. set a longer JWT_SECRET.");
            }
            this.signingKey = Keys.hmacShaKeyFor(keyBytes);
            this.expiration = expiration;
        }


    /**
     * This will generate a signed JWT for a user.
     * 
     * @param email (The user's email, used as the token subject.)
     * @param role (The user's role, stored as a claim.)
     * @return (Returns a signed JWT string.)
     */
    public String generateToken(String email, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration);

        return Jwts.builder()
                    .subject(email)
                    .claim("role", role)
                    .issuedAt(now)
                    .expiration(expiry)
                    .signWith(signingKey)
                    .compact();  
    }

    /**
     * This parses and cryptographically verifies a token, returning its claims.
     * It is the single place where a token is validated, so the signature and expiration checks
     * cannot drift between callers.
     * 
     * @param token (The JWT string.)
     * @return (Returns the verified claims.)
     * @throws JwtException (Throws if the token is malformed, tampered, or expired.)
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


     /**
      * This extracts the email (subject) from a verified token.
      * 
      * @param token (The JWT string.)
      * @return (Returns the email, or null if the token is invalid.)
     */
    public String extractEmail(String token){
        try {
            return parseClaims(token).getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * This extracts the role claim from a verified token.
     * 
     * @param token (The JWT string.)
     * @return (Returns the role, or null of the token is invalid.)
     */
    public String extractRole(String token) {
        try {
            return parseClaims(token).get("role", String.class);
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
            }
        
    /**
     * validates a token: checks signature and expiration. 
     * 
     * @param token (the JWT string.)
     * @return true if the token is valid and not expired, otherwise false.
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
