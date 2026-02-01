package org.homanhquan.productservice.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.productservice.enums.Role;
import org.homanhquan.productservice.service.TokenBlacklistService;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        log.info("Request: {} {}", method, path);

        // ===== PUBLIC ENDPOINTS =====
        if (isPublicEndpoint(path, method)) {
            log.info("Public endpoint, skip JWT");
            filterChain.doFilter(request, response);
            return;
        }

        // ===== PROTECTED ENDPOINTS - Need JWT =====
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || authHeader.isEmpty()) {
            log.warn("Missing token for: {} {}", method, path);
            sendErrorResponse(response, "Please provide a valid JWT token",
                    HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (!authHeader.startsWith("Bearer ")) {
            sendErrorResponse(response, "Invalid token format",
                    HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String jwt = authHeader.substring(7);

        // CHECK BLACKLIST FIRST
        if (tokenBlacklistService.isBlacklisted(jwt)) {
            log.warn("Token is blacklisted (revoked/logged out)");
            sendErrorResponse(response, "Token has been revoked",
                    HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            Claims claims = jwtUtil.validateToken(jwt);

            String userId = claims.get("userId", String.class);
            String cartId = claims.get("cartId", String.class);
            String username = claims.getSubject();
            String role = claims.get("role", String.class);

            log.info("Authenticated: {} ({})", username, role);

            // ===== ROLE-BASED ACCESS CONTROL =====
            if (requiresAdminRole(path, method)) {
                if (!"ADMIN".equals(role)) {
                    log.warn("Access denied: {} is not ADMIN", username);
                    sendErrorResponse(response,
                            "You don't have permission to access this resource",
                            HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
            }

            // ✅ Tạo CustomUserDetails với đầy đủ thông tin
            Role roleEnum = Role.valueOf(role);
            CustomUserDetails userDetails;

            if (roleEnum == Role.ADMIN || roleEnum == Role.CHEF) {
                userDetails = CustomUserDetails.builder()
                        .id(UUID.fromString(userId))
                        .username(username)
                        .role(Role.valueOf(role))
                        .build();
            } else {
                userDetails = CustomUserDetails.builder()
                        .id(UUID.fromString(userId))
                        .cartId(UUID.fromString(cartId))
                        .username(username)
                        .role(Role.valueOf(role))
                        .build();
            }

            // ✅ Set CustomUserDetails làm principal
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,  // Principal là CustomUserDetails (có id, username, role)
                            null,         // Credentials
                            userDetails.getAuthorities() // Authorities từ CustomUserDetails
                    );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Continue filter chain
            filterChain.doFilter(request, response);

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.error("Token expired: {}", e.getMessage());
            sendErrorResponse(response, "Token has expired",
                    HttpServletResponse.SC_UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Invalid token: {}", e.getMessage());
            sendErrorResponse(response, "Invalid token",
                    HttpServletResponse.SC_UNAUTHORIZED);
        } finally {
            // ✅ Clear context sau khi xử lý xong
            SecurityContextHolder.clearContext();
        }
    }

    private boolean isPublicEndpoint(String path, String method) {
        if (path.startsWith("/auth/login") || path.startsWith("/auth/register")) {
            return true;
        }

        if ("GET".equals(method) && path.startsWith("/api/product")) {
            return true;
        }

        return false;
    }

    private boolean requiresAdminRole(String path, String method) {
        if (path.startsWith("/api/product")) {
            if ("POST".equals(method) ||
                    "PUT".equals(method) ||
                    "DELETE".equals(method)) {
                return true;
            }
        }

        if (path.startsWith("/brand")) {
            return true;
        }

        return false;
    }

    private void sendErrorResponse(HttpServletResponse response, String message, int status)
            throws IOException {

        response.setStatus(status);
        response.setContentType("application/json");

        String errorType = (status == HttpServletResponse.SC_UNAUTHORIZED)
                ? "Unauthorized" : "Forbidden";

        String errorJson = String.format(
                "{\"error\":\"%s\",\"message\":\"%s\",\"path\":\"%s\",\"status\":%d}",
                errorType,
                message,
                "",
                status
        );

        response.getWriter().write(errorJson);
        response.getWriter().flush();
    }
}