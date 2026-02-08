package org.homanhquan.productservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.productservice.security.helper.JwtAuthenticationFilterHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtAuthenticationFilterHelper jwtAuthenticationFilterHelper;

    /**
     * JWT Authentication Filter:
     * - Extract JWT from Authorization header.
     * - Skip if no token (public endpoints allowed).
     * - Validate token is not blacklisted.
     * - Validate JWT and set Authentication in SecurityContext.
     * - Continue filter chain for authorization check.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String jwt = jwtAuthenticationFilterHelper.resolveToken(request);

        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        jwtAuthenticationFilterHelper.checkBlacklist(jwt);
        jwtAuthenticationFilterHelper.authenticateToken(jwt, request);
        filterChain.doFilter(request, response);
    }
}