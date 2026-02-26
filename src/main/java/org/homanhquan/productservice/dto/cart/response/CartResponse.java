package org.homanhquan.productservice.dto.cart.response;

import lombok.Builder;
import org.homanhquan.productservice.dto.cartItem.response.CartItemResponse;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record CartResponse(
        List<CartItemResponse> items,
        BigDecimal totalPrice
) {
}
