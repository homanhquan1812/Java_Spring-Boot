package org.homanhquan.productservice.builder.product.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.homanhquan.productservice.dto.product.request.UpdateProductRequest;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateProductRequestBuilder {

    public static UpdateProductRequest updateProductRequest() {
        return UpdateProductRequest.builder()
                .name("New Product")
                .description("Description")
                .price(BigDecimal.valueOf(200.00))
                .build();
    }
}
