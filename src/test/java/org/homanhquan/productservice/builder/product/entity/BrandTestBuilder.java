package org.homanhquan.productservice.builder.product.entity;

import org.homanhquan.productservice.entity.Brand;
import org.springframework.test.util.ReflectionTestUtils;

public class BrandTestBuilder {

    public static Brand brand() {
        Brand brand = new Brand();
        ReflectionTestUtils.setField(brand, "id", 1L);
        ReflectionTestUtils.setField(brand, "name", "Test Brand");

        return brand;
    }
}
