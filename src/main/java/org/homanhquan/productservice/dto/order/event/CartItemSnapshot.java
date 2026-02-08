package org.homanhquan.productservice.dto.order.event;

import java.math.BigDecimal;

public record CartItemSnapshot(
        String productId,
        String name,
        BigDecimal price,
        int quantity
) {
}
