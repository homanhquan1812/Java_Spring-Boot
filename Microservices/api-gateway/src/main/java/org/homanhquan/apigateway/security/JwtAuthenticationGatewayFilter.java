package org.homanhquan.apigateway.security;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.apigateway.service.TokenBlacklistService;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationGatewayFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        HttpMethod method = request.getMethod();

        log.info("Gateway: {} {}", method, path);

        // ===== PUBLIC ENDPOINTS =====
        if (isPublicEndpoint(path, method)) {
            log.info("Public endpoint, skip JWT");
            return chain.filter(exchange);
        }

        // ===== PROTECTED ENDPOINTS - Need JWT =====
        List<String> authHeaders = request.getHeaders().get("Authorization");

        if (authHeaders == null || authHeaders.isEmpty()) {
            log.warn("Missing token for: {} {}", method, path);
            return onError(exchange, "Please provide a valid JWT token", HttpStatus.UNAUTHORIZED);
        }

        String authHeader = authHeaders.get(0);

        if (!authHeader.startsWith("Bearer ")) {
            return onError(exchange, "Invalid token format", HttpStatus.UNAUTHORIZED);
        }

        String jwt = authHeader.substring(7);

        // CHECK BLACKLIST FIRST
        if (tokenBlacklistService.isBlacklisted(jwt)) {
            log.warn("Token is blacklisted (revoked/logged out)");
            return onError(exchange, "Token has been revoked", HttpStatus.UNAUTHORIZED);
        }

        try {
            Claims claims = jwtUtil.validateToken(jwt);

            String userId = claims.get("userId", String.class);
            String username = claims.getSubject();
            String role = claims.get("role", String.class);

            log.info("Authenticated: {} ({})", username, role);

            // ===== ROLE-BASED ACCESS CONTROL =====
            if (requiresAdminRole(path, method)) {
                if (!"ADMIN".equals(role)) {
                    log.warn("Access denied: {} is not ADMIN", username);
                    return onError(exchange, "You don't have permission to access this resource",
                            HttpStatus.FORBIDDEN);
                }
            }

            // Forward with user info headers
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", userId != null ? userId : "")
                    .header("X-User-Name", username)
                    .header("X-User-Role", role)
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.error("Token expired: {}", e.getMessage());
            return onError(exchange, "Token has expired", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Invalid token: {}", e.getMessage());
            return onError(exchange, "Invalid token", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Define public endpoints that don't need JWT
     */
    private boolean isPublicEndpoint(String path, HttpMethod method) {
        // Auth endpoints - always public
        if (path.startsWith("/auth/login") || path.startsWith("/auth/register")) {
            return true;
        }

        // GET /api/product/** - public (anyone can view)
        if (HttpMethod.GET.equals(method) && path.startsWith("/api/product")) {
            return true;
        }

        return false;
    }

    /**
     * Check if endpoint requires ADMIN role
     */
    private boolean requiresAdminRole(String path, HttpMethod method) {
        // POST/PUT/DELETE /api/product/** - ADMIN only
        if (path.startsWith("/api/product")) {
            if (HttpMethod.POST.equals(method) ||
                    HttpMethod.PUT.equals(method) ||
                    HttpMethod.DELETE.equals(method)) {
                return true;
            }
        }

        // /brand/** - ADMIN only
        if (path.startsWith("/brand")) {
            return true;
        }

        return false;
    }

    /**
     * Send error response
     */
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", "application/json");

        String errorJson = String.format(
                "{\"error\":\"%s\",\"message\":\"%s\",\"path\":\"%s\",\"status\":%d}",
                status.equals(HttpStatus.UNAUTHORIZED) ? "Unauthorized" : "Forbidden",
                message,
                exchange.getRequest().getPath().value(),
                status.value()
        );

        return response.writeWith(Mono.just(
                response.bufferFactory().wrap(errorJson.getBytes())
        ));
    }

    @Override
    public int getOrder() {
        return -100; // Execute before routing
    }
}