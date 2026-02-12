package org.homanhquan.productservice.builder.product.entity;

import org.homanhquan.productservice.entity.Brand;
import org.homanhquan.productservice.entity.Product;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;


public class ProductTestBuilder {

    public static Product buildProduct() {
        Product product = Product.builder()
                .name("New Product")
                .description("Description")
                .price(BigDecimal.valueOf(200.00))
                .brand(new Brand())
                .build();

        ReflectionTestUtils.setField(product, "id", 1L);

        return product;
    }
    /*
    public static Product product() {
        Product product = new Product();
        ReflectionTestUtils.setField(product, "id", 1L);
        ReflectionTestUtils.setField(product, "name", "Test Product");
        ReflectionTestUtils.setField(product, "description", "Test Description");
        ReflectionTestUtils.setField(product, "price", BigDecimal.valueOf(100.00));
        ReflectionTestUtils.setField(product, "status", Status.ACTIVE);
        ReflectionTestUtils.setField(product, "createdBy", UUID.randomUUID());
        ReflectionTestUtils.setField(product, "updatedBy", UUID.randomUUID());
        ReflectionTestUtils.setField(product, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(product, "updatedAt", LocalDateTime.now());

        return product;
    }

     */
}
