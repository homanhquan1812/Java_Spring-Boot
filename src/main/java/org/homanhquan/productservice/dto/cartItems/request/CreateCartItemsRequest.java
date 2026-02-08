package org.homanhquan.productservice.dto.cartItems.request;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateCartItemsRequest(
        String productId,
        String name,
        BigDecimal price,
        Integer quantity
) {
}
