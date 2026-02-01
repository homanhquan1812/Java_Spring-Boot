package org.homanhquan.apigateway.service;

public interface TokenBlacklistService {
    /**
     * Check if token is blacklisted
     * @param token JWT token to check
     * @return true if blacklisted, false otherwise
     */
    boolean isBlacklisted(String token);
}