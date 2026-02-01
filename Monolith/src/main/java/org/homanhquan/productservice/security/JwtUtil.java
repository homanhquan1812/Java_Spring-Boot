package org.homanhquan.productservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.productservice.enums.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey key;
    private final long EXPIRATION = 1000 * 60 * 60 * 24; // 24h

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // ========================================
    // TOKEN GENERATION
    // ========================================

    public String generateToken(CustomUserDetails userDetails) {
        Date now = new Date();

        if (userDetails.getRole() == Role.ADMIN || userDetails.getRole() == Role.CHEF) {
            return Jwts.builder()
                    .subject(userDetails.getUsername())
                    .claim("userId", userDetails.getId().toString())
                    .claim("role", userDetails.getRole().name())
                    .issuedAt(now)
                    .expiration(new Date(now.getTime() + EXPIRATION))
                    .signWith(key)
                    .compact();
        }

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("userId", userDetails.getId().toString())
                .claim("role", userDetails.getRole().name())
                .claim("cartId", userDetails.getCartId().toString())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + EXPIRATION))
                .signWith(key)
                .compact();
    }

    /**
     * Validates token and throws exception if invalid/expired.
     * Used in Filter to catch exception
     */
    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token expired", e);
        } catch (JwtException e) {
            throw new RuntimeException("Invalid JWT", e);
        }
    }

    /**
     * Gets username and validates token:
     * getUsernameFromToken -> throws exception if invalid
     * getUsernameFromTokenSafe -> returns null if invalid
     */
    public String getUsernameFromToken(String token) {
        return validateToken(token).getSubject();
    }

    public String getUsernameFromTokenSafe(String token) {
        try {
            return extractAllClaims(token).getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    // ========================================
    // CORE PARSING - Parse CHỈ 1 LẦN
    // ========================================

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token expired", e);
        } catch (JwtException e) {
            throw new RuntimeException("Invalid JWT", e);
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public long getRemainingTime(String token) {
        try {
            Date expiration = extractExpiration(token);
            long remaining = expiration.getTime() - System.currentTimeMillis();
            return Math.max(0, remaining);
        } catch (Exception e) {
            return 0;
        }
    }
}