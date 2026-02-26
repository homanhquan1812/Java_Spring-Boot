package org.homanhquan.productservice.event.order;

import java.math.BigDecimal;

public record CartItemSnapshot(
        Long productId,
        String name,
        BigDecimal price,
        int quantity
) {
}
