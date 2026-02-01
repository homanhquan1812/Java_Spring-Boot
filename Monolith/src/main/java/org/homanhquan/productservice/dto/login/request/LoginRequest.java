package org.homanhquan.productservice.dto.login.request;

public record LoginRequest(
        String username,
        String password
        // String sessionId; // For session
) {}