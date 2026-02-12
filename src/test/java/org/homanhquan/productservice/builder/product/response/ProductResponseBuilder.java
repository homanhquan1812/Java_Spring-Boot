package org.homanhquan.productservice.builder.product.response;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.homanhquan.productservice.dto.product.response.ProductResponse;
import org.homanhquan.productservice.enums.Status;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductResponseBuilder {

    public static ProductResponse productResponse() {
        return ProductResponse.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .price(BigDecimal.valueOf(100.00))
                .status(Status.ACTIVE)
                .brandName("Test Brand")
                .build();
    }
}
