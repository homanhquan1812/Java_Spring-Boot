package org.homanhquan.productservice.security.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.productservice.enums.Role;
import org.homanhquan.productservice.security.userDetails.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenAuthentication {

    private final JwtUtil jwtUtil;

    public void authenticateToken(String jwt, HttpServletRequest request) {
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
