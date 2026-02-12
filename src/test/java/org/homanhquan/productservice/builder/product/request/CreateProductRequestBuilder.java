package org.homanhquan.productservice.builder.product.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.homanhquan.productservice.dto.product.request.CreateProductRequest;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateProductRequestBuilder {

    public static CreateProductRequest createProductRequest() {
        return CreateProductRequest.builder()
                .name("New Product")
                .description("Description")
                .price(BigDecimal.valueOf(200.00))
                .brandId(1L)
                .build();
    }
}