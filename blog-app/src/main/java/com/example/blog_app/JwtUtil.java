package com.example.blog_app;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Utility class for handling various JWT operations
 * Responsible for generating, validating, and parsing tokens
 */
@Component
public class JwtUtil {
    private final SecretKey secretKey;

    /**
     * Initialize with secret key from application.properties
     */
    public JwtUtil(@Value("${app.jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Generates a JWT token for the given username
     * Expires after 1 hour
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600_000))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Validate token signature and expiration.
     * @return True if valid, False if expired/tampered.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * Extract username from token.
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}
