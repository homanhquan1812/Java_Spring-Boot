package org.homanhquan.productservice.config.cache;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;

import static org.homanhquan.productservice.common.constants.ProductCacheConstants.ALL_PRODUCTS;
import static org.homanhquan.productservice.common.constants.ProductCacheConstants.PRODUCT_BY_ID;

/**
 * Annotation explanation:
 * - @Slf4j: Lombok annotation that auto-generates a logger instance (log) for the class.
 * - @EnableCaching: Activates Spring's annotation-driven caching mechanism (e.g. @Cacheable, @CachePut, @CacheEvict).
 * - @Configuration: Marks a class as a source of bean definitions.
 * - @Bean: Marks a method inside @Configuration class to define and return a Spring bean.
 * - @Profile("dev"): Marks a bean or configuration class to be active only for specific environment profiles (dev/prod).
 * ==================================================
 * Why use Redis over HashMap?
 * - Previously, HashMap was used to store data temporarily in RAM because it was fast and easy to implement.
 * - However, this approach is not distributed, making it unsuitable for microservices, and all cached data is lost when the system restarts.
 * - Redis overcomes these limitations by:
 *   + Supporting distributed caching.
 *   + Providing persistence to disk.
 *   + Enabling automatic expiration through TTL.
 *   + Offering pub/sub capabilities for message brokering.
 */
@Slf4j
@Configuration
@EnableCaching
@ConfigurationProperties(prefix = "cache.ttl")
@Getter
@Setter
public class RedisConfig {

    private int defaultMinutes;
    private int allProductsMinutes;
    private int productByIdMinutes;

    /**
     * Handles automatic serialization/deserialization and TTL management for cached data.
     * ==================================================
     * Method explanation:
     * - entryTtl(Duration.ofMinutes(15)): Set the DEFAULT time-to-live (TTL) for cache entries to 15 minutes.
     * - disableCachingNullValues(): Prevents null results from being stored in Redis.
     * - serializeKeysWith(): Serializes cache keys as plain strings for readability and easy debugging via Redis CLI.
     * - serializeValuesWith(): Serializes values as JSON with embedded @class type metadata for deserialization.
     *   Renaming/moving the class will invalidate existing cached data.
     * - Map<String, RedisCacheConfiguration>: Defines per-cache custom configurations & overrides the default TTL for specific caches.
     * - return...: Creates a Redis cache manager with default rules and per-cache overrides.
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(defaultMinutes))
                .disableCachingNullValues()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(RedisSerializer.string())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(RedisSerializer.json())
                );

        Map<String, RedisCacheConfiguration> configs = Map.of(
                ALL_PRODUCTS, defaultConfig.entryTtl(Duration.ofMinutes(allProductsMinutes)),
                PRODUCT_BY_ID, defaultConfig.entryTtl(Duration.ofMinutes(productByIdMinutes))
        );

        return RedisCacheManager.builder(factory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(configs)
                .build();
    }

    /**
     * redisTemplate is used for custom Redis operations (e.g. token blacklisting, session management, or manual key-value storage)
     * using Key-Value pairs stored as plain strings:
     * - Key: A string identifier used to locate data in Redis (e.g. "product:1").
     * - Value: The string data stored for that key (e.g. { "id": 1, "name": "iPhone" }).
     * @Cacheable follows Redis Key format: <cacheName>::<generatedKey> (e.g. "productById::1").
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

    /**
     * Test Redis connection. If successful, return response: PONG.
     * Currently, this feature only runs in "dev" environment.
     */
    @Bean
    @Profile("dev")
    public CommandLineRunner testRedisConnection(RedisConnectionFactory factory) {
        return args -> {
            try {
                String pong = factory.getConnection().ping();
                log.info("Redis connection successful! Response: {}", pong);
            } catch (Exception e) {
                log.error("Redis connection failed: {}", e.getMessage());
                log.error("Make sure Redis is running on the configured host/port");
            }
        };
    }
}
