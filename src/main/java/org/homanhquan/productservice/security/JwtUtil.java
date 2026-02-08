package org.homanhquan.productservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

/**
 * JwtUtil's main functions explanation:
 * - JwtUtil: Constructor that gets the secret key from application.yml.
 * - generateToken(): Used by Login Service, generates a token based on information from CustomUserDetails after successful authentication.
 * - validateToken(): Used by JWT Authentication Filter, validate the token and returns Claims for building Authentication object.
 * - getRemainingTime(): Used by Token Blacklist Service, calculates time until token expires (in milliseconds) for setting Redis TTL.
 */
@Slf4j
@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expiration;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    public String generateToken(CustomUserDetails customUserDetails) {
        Date now = new Date();

        return Jwts.builder()
                .subject(customUserDetails.getUsername())
                .claim("userId", customUserDetails.getId().toString())
                .claim("role", customUserDetails.getRole().name())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration))
                .signWith(key)
                .compact();
    }

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

    public long getRemainingTime(String token) {
        try {
            Date expiration = extractExpiration(token);
            long remaining = expiration.getTime() - System.currentTimeMillis();
            return Math.max(0, remaining);
        } catch (Exception e) {
            return 0;
        }
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }
}