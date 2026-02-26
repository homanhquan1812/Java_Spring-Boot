package org.homanhquan.productservice.security.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static org.homanhquan.productservice.common.constants.RedisConstants.BLACKLIST_PREFIX;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;

    public boolean isBlacklisted(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        try {
            String key = BLACKLIST_PREFIX + token;
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("Error checking blacklist: {}", e.getMessage());
            return true;
        }
    }

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