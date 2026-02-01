package org.homanhquan.authservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.authservice.security.JwtUtil;
import org.homanhquan.authservice.service.TokenBlacklistService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl implements TokenBlacklistService {
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;
    private static final String BLACKLIST_PREFIX = "blacklist:token:";

    @Override
    public void blacklistToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            log.debug("Attempted to blacklist null/empty token");
            return;
        }

        try {
            long ttl = jwtUtil.getRemainingTime(token);

            if (ttl > 0) {
                String key = BLACKLIST_PREFIX + token;
                redisTemplate.opsForValue().set(key, "revoked", ttl, TimeUnit.MILLISECONDS);
                log.info("Token blacklisted successfully. TTL: {} seconds", ttl / 1000);
            } else {
                log.debug("Token already expired, skip blacklisting");
            }
        } catch (Exception e) {
            log.error("Failed to blacklist token: {}", e.getMessage());
            throw new RuntimeException("Failed to blacklist token", e);
        }
    }
}