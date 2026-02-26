package org.homanhquan.productservice.security.rateLimitFilter;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.homanhquan.productservice.exception.helper.global.error.response.ErrorResponseHelper;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "rate-limit")
@Getter
@Setter
public class RateLimitBucketService {

    private int capacity;
    private int refillDurationSeconds;
    private int maxCacheSize;
    private int cacheEntryTtlMinutes;

    private final ErrorResponseHelper errorResponseHelper;

    private Cache<String, Bucket> cache;

    @PostConstruct
    public void initCache() {
        this.cache = Caffeine.newBuilder()
                .maximumSize(maxCacheSize)
                .expireAfterAccess(cacheEntryTtlMinutes, TimeUnit.MINUTES)
                .build();
    }

    public Bucket resolveBucket(String ip) {
        return cache.get(ip, k -> createNewBucket());
    }

    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(capacity)
                .refillIntervally(capacity, Duration.ofSeconds(refillDurationSeconds))
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
