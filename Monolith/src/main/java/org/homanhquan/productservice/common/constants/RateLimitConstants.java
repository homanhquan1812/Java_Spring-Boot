package org.homanhquan.productservice.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Duration;

@NoArgsConstructor(access = AccessLevel.PRIVATE) // A constants class should not be instantiated
public class RateLimitConstants {

    public static final int CAPACITY = 5;
    public static final Duration REFILL_DURATION = Duration.ofMinutes(1);
    public static final int MAX_CACHE_SIZE = 10000;
    public static final Duration CACHE_ENTRY_TTL = Duration.ofMinutes(10);
}
