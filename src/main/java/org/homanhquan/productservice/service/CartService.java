package org.homanhquan.productservice.service;

import org.homanhquan.productservice.dto.cart.response.CartResponse;
import org.homanhquan.productservice.dto.cartItems.request.CreateCartItemsRequest;
import org.homanhquan.productservice.dto.cartItems.response.CartItemsResponse;

import java.util.UUID;

public interface CartService {

    CartResponse getCartItems(UUID userId);
    CartItemsResponse createCartItems(UUID userId, CreateCartItemsRequest createCartItemsRequest);
    void deleteCartItems(UUID userId, UUID cartItemId);
}
