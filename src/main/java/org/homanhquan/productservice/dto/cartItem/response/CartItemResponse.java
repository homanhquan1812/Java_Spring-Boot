package org.homanhquan.productservice.dto.cartItem.response;

import lombok.Builder;

@Builder
public record CartItemResponse(
        String name,
        Integer price,
        Integer quantity
) {
}
