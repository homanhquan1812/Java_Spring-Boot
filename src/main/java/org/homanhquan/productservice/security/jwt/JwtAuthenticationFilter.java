package org.homanhquan.productservice.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenAuthentication tokenAuthentication;
    private final TokenBlacklistService tokenBlacklistService;

    /**
     * Flow:
     * 1. Extract JWT from Authorization header.
     * 2. Skip if no token (public endpoints allowed).
     * 3. Validate token is not blacklisted.
     * 4. Validate JWT and set Authentication in SecurityContext.
     * 5. Continue filter chain for authorization check.
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String jwt = resolveToken(request);

        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (tokenBlacklistService.isBlacklisted(jwt)) {
            log.warn("Blocked blacklisted token");
            throw new CredentialsExpiredException("Token has been revoked");
        }

        tokenAuthentication.authenticateToken(jwt, request);
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }
}