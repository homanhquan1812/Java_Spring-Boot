package org.homanhquan.productservice.dto.orderItem.response;

import java.time.LocalDateTime;

public record OrderItemsResponse(
        String name,
        Integer price,
        Integer quantity,
        LocalDateTime createdAt
) {
}
