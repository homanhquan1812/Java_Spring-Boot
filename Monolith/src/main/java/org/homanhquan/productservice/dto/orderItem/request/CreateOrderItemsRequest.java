package org.homanhquan.productservice.dto.orderItem.request;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateOrderItemsRequest(
        UUID orderId,
        UUID productId,
        String name,
        BigDecimal price,
        Integer quantity
) {
}
