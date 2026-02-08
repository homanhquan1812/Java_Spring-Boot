package org.homanhquan.productservice.security.helper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.productservice.enums.Role;
import org.homanhquan.productservice.security.CustomUserDetails;
import org.homanhquan.productservice.security.JwtUtil;
import org.homanhquan.productservice.service.TokenBlacklistService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilterHelper {

    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    public String resolveToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }

    public void checkBlacklist(String jwt) {
        if (!tokenBlacklistService.isBlacklisted(jwt)) {
            return;
        }

        log.warn("Blocked blacklisted token");
        throw new CredentialsExpiredException("Token has been revoked");
    }

    public void authenticateToken(String jwt, HttpServletRequest request) {
        try {
            Claims claims = jwtUtil.validateToken(jwt);
            CustomUserDetails userDetails = buildUserDetails(claims);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info("Authenticated: {} ({})",
                    userDetails.getUsername(),
                    userDetails.getRole());
        } catch (ExpiredJwtException e) {
            log.error("Token has expired: {}", e.getMessage());
            throw new CredentialsExpiredException("Token has expired", e);
        } catch (Exception e) {
            log.error("Invalid token: {}", e.getMessage());
            throw new BadCredentialsException("Invalid token", e);
        }
    }

    public CustomUserDetails buildUserDetails(Claims claims) {
        String userId = claims.get("userId", String.class);
        String username = claims.getSubject();
        Role role = Role.valueOf(claims.get("role", String.class));

        return CustomUserDetails.builder()
                .id(UUID.fromString(userId))
                .username(username)
                .role(role)
                .build();
    }
}
