package org.homanhquan.productservice.dto.cart.response;

import lombok.Builder;
import org.homanhquan.productservice.dto.cartItems.response.CartItemsResponse;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record CartResponse(
        List<CartItemsResponse> items,
        BigDecimal totalPrice
) {
}
