package org.homanhquan.productservice.dto.cartItems.response;

import lombok.Builder;

@Builder
public record CartItemsResponse(
        String name,
        Integer price,
        Integer quantity
) {
}
