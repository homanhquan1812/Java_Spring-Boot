package org.homanhquan.productservice.service;

import org.homanhquan.productservice.dto.cart.response.CartResponse;
import org.homanhquan.productservice.dto.cartItem.request.CreateCartItemsRequest;
import org.homanhquan.productservice.dto.cartItem.response.CartItemResponse;
import org.homanhquan.productservice.dto.order.request.CheckoutRequest;
import org.homanhquan.productservice.dto.order.response.OrderResponse;

import java.util.UUID;

public interface CartService {

    CartResponse get(UUID userId);
    OrderResponse create(UUID userId, CheckoutRequest request);
    CartItemResponse addItem(UUID userId, CreateCartItemsRequest request);
    void deleteByCartItemId(UUID userId, UUID cartItemId);
}
