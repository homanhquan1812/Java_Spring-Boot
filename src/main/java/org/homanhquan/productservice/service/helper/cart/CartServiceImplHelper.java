package org.homanhquan.productservice.service.helper.cart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.productservice.entity.Cart;
import org.homanhquan.productservice.entity.CartItem;
import org.homanhquan.productservice.exception.ResourceNotFoundException;
import org.homanhquan.productservice.repository.CartItemsRepository;
import org.homanhquan.productservice.repository.CartRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CartServiceImplHelper {

    private final CartRepository cartRepository;
    private final CartItemsRepository cartItemsRepository;

    public Cart findCart(UUID userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + userId));
    }

    public CartItem findCartItem(UUID cartItemId) {
        return cartItemsRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));
    }
}
