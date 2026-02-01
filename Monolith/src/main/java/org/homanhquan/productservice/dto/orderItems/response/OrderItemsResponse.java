package org.homanhquan.productservice.dto.orderItems.response;

import java.time.LocalDateTime;

public record OrderItemsResponse(
        String name,
        Integer price,
        Integer quantity,
        LocalDateTime createdAt
) {
}
