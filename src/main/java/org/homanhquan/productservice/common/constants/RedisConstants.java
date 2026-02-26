package org.homanhquan.productservice.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE) // A constants class should not be instantiated
public class RedisConstants {

    public static final String BLACKLIST_PREFIX = "blacklist:token:";
}
