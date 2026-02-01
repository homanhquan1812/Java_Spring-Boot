package org.homanhquan.authservice.service;

public interface TokenBlacklistService {
    /**
     * Blacklist a JWT token until it expires
     * @param token JWT token to blacklist
     * @throws RuntimeException if blacklisting fails
     */
    void blacklistToken(String token);
}