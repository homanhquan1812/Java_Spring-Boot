package org.homanhquan.productservice.dto.cartItem.request;

import java.math.BigDecimal;

public record CreateCartItemsRequest(
        String productId,
        String name,
        BigDecimal price,
        Integer quantity
) {
}
