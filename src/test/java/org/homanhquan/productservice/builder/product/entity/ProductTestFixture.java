package org.homanhquan.productservice.builder.product.entity;

import org.homanhquan.productservice.entity.Product;
import org.homanhquan.productservice.enums.Status;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

public class ProductTestFixture {

    public static Product buildProduct() {
        Product product = Product.of(
                "New Product",
                "Description",
                Status.ACTIVE,
                BigDecimal.valueOf(200.00),
                null,
                null
        );

        ReflectionTestUtils.setField(product, "id", 1L);

        return product;
    }
}
