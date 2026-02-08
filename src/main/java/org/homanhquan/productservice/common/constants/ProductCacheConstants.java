package org.homanhquan.productservice.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE) // A constants class should not be instantiated
public class ProductCacheConstants {

    public static final String ALL_PRODUCTS = "allProducts";
    public static final String PRODUCT_BY_ID = "productById";
}
