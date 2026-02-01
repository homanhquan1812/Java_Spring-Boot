package org.homanhquan.productservice.config.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.util.Map;

import static org.homanhquan.productservice.common.constants.ProductCacheConstants.ALL_PRODUCTS;
import static org.homanhquan.productservice.common.constants.ProductCacheConstants.PRODUCT_BY_ID;

@Slf4j
@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(15))
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
                ALL_PRODUCTS, defaultConfig.entryTtl(Duration.ofMinutes(30)),
                PRODUCT_BY_ID, defaultConfig.entryTtl(Duration.ofMinutes(15))
        );

        return RedisCacheManager.builder(factory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(configs)
                .build();
    }

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
