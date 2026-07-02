package com.richenterprises.banking_api.util;

import io.jsonwebtoken.Claims;
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
     * The signing secret.
     * It is injected from application.properties
     * It requires at least 256 bits (32 characters) for HS256.
     * Secrets are never hardcoded, and are never committed to Git.
     */
    @Value("${jwt.secret}")
    private String secret;
        
    /**
    * Token expiration time in milliseconds.
    * It is injected from application.properties
    * Default: 15 minutes = 900,000 ms.
    */
   @Value("${jwt.expiration:900000}")
   private long expiration;
   
    /**
    * This will genrate a SecretKey from the configured secret string.
    * The Keys.hmacShaKeyFor ensures the key is the correct length for HS256.
    */
   private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
   }

   /**
    * This will generate a JWT token for a user.
    * 
    * @param email (The user's email used as the subject/indentifier.)
    * @param role (The user's role stored as a claim.)
    * @return (Returns a signed JWT string)
    */
    public String generateToken(String email, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration);

        return Jwts.builder()
                        .subject(email)
                        .claim("role", role)
                        .issuedAt(now)
                        .expiration(expiry)
                        .signWith(getSigningKey())
                        .compact();
                    }
                
                    /**
                     * This extracts the email (subject) from a token. 
                     * It does not validate if the token is expired. 
                     * Use the validateToken instead.
                    */
    public String extractEmail(String token){
        try {
            Claims claims = Jwts.parser()
                            .verifyWith(getSigningKey())
                            .build().parseSignedClaims(token)
                            .getPayload();
            return claims.getSubject();

        } catch (Exception e) {
                    return null;
                }
            }
        
     /**
      * This will extract the role from a token. 
      * 
      * @param token (The JWT string.)
      * @return the role string, or null if the token is malformed. 
     */
    public String extractRole(String token) {
        try {
            Claims claims = Jwts.parser()
                            .verifyWith(getSigningKey())
                            .build()
                            .parseSignedClaims(token)
                            .getPayload();
            return claims.get("role", String.class);

        } catch (Exception e) {
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
            Jwts.parser()
                        .verifyWith(getSigningKey())
                        .build()
                        .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
           
    
}
