package org.homanhquan.productservice.service;

public interface TokenBlacklistService {
    /**
     * Blacklist a JWT token until it expires
     * @param token JWT token to blacklist
     * @throws RuntimeException if blacklisting fails
     */
    void blacklistToken(String token);

    /**
     * Check if token is blacklisted
     * @param token JWT token to check
     * @return true if blacklisted, false otherwise
     */
    boolean isBlacklisted(String token);
}