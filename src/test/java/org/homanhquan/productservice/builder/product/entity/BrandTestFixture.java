package org.homanhquan.productservice.builder.product.entity;

import org.homanhquan.productservice.entity.Brand;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalTime;

public class BrandTestFixture {

    public static Brand buildBrand() {
        Brand brand = Brand.of(
                "Test Brand",
                "Test Description",
                "Test Address",
                LocalTime.of(8, 0),
                LocalTime.of(17, 0)
        );

        ReflectionTestUtils.setField(brand, "id", 1L);

        return brand;
    }
}
