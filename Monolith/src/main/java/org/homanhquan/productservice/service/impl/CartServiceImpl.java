package org.homanhquan.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.homanhquan.productservice.dto.cart.response.CartResponse;
import org.homanhquan.productservice.projection.CartItemsProjection;
import org.homanhquan.productservice.dto.cartItems.request.CreateCartItemsRequest;
import org.homanhquan.productservice.dto.cartItems.response.CartItemsResponse;
import org.homanhquan.productservice.entity.CartItems;
import org.homanhquan.productservice.exception.ResourceNotFoundException;
import org.homanhquan.productservice.mapper.CartItemsMapper;
import org.homanhquan.productservice.mapper.CartMapper;
import org.homanhquan.productservice.repository.CartItemsRepository;
import org.homanhquan.productservice.repository.CartRepository;
import org.homanhquan.productservice.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final CartItemsMapper cartItemsMapper;
    private final CartItemsRepository cartItemsRepository;

    @Override
    @Transactional(readOnly = true)
    public CartResponse getProductsInCart(UUID userId) {
        cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user"));

        List<CartItemsProjection> cartItemsProjections = cartRepository.findCartWithSpecificItemsById(userId);

        BigDecimal totalPrice = cartItemsProjections.stream()
                .map(item -> item.getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .items(cartMapper.projectionToDtoList(cartItemsProjections))
                .totalPrice(totalPrice)
                .build();
    }

    @Override
    public CartItemsResponse createCartItems(UUID userId, UUID cartId, CreateCartItemsRequest createCartItemsRequest) {
        CartItems cartItems = cartItemsMapper.toEntity(createCartItemsRequest);
        cartItems.setCartId(cartId);
        return cartItemsMapper.toDto(cartItemsRepository.save(cartItems));
    }

    @Override
    public void deleteCartItems(UUID userId, UUID cartItemId) {
        CartItems cartItems = cartItemsRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));
        cartItemsRepository.delete(cartItems);
    }
}
