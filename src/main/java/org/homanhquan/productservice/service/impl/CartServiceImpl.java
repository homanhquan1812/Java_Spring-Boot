package org.homanhquan.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.productservice.dto.cart.response.CartResponse;
import org.homanhquan.productservice.entity.Cart;
import org.homanhquan.productservice.entity.CartItem;
import org.homanhquan.productservice.projection.CartItemProjection;
import org.homanhquan.productservice.dto.cartItems.request.CreateCartItemsRequest;
import org.homanhquan.productservice.dto.cartItems.response.CartItemsResponse;
import org.homanhquan.productservice.exception.ResourceNotFoundException;
import org.homanhquan.productservice.mapper.CartItemsMapper;
import org.homanhquan.productservice.mapper.CartMapper;
import org.homanhquan.productservice.repository.CartItemsRepository;
import org.homanhquan.productservice.repository.CartRepository;
import org.homanhquan.productservice.repository.UserRepository;
import org.homanhquan.productservice.service.CartService;
import org.homanhquan.productservice.service.helper.cart.CartServiceImplHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final CartItemsMapper cartItemsMapper;
    private final CartItemsRepository cartItemsRepository;
    private final CartServiceImplHelper cartHelper;

    @Override
    public CartResponse getCartItems(UUID userId) {
        cartHelper.findCart(userId);
        List<CartItemProjection> cartItemProjections = cartRepository.findCartWithSpecificItemsById(userId);

        BigDecimal totalPrice = cartItemProjections.stream()
                .map(item -> item.getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .items(cartMapper.projectionToDtoList(cartItemProjections))
                .totalPrice(totalPrice)
                .build();
    }

    @Override
    public CartItemsResponse createCartItems(UUID userId, CreateCartItemsRequest createCartItemsRequest) {
        CartItem cartItem = cartItemsMapper.toEntity(createCartItemsRequest);
        Cart cart = cartHelper.findCart(userId);
        cartItem.setCartId(cart.getId());

        log.info("Cart item {} added into cart successfully by {}.",
                cartItem.getId(),
                userId
        );

        return cartItemsMapper.toDto(cartItemsRepository.save(cartItem));
    }

    @Override
    public void deleteCartItems(UUID userId, UUID cartItemId) {
        log.warn("WARNING: User {} is deleting cart item {}!",
                userId,
                cartItemId
        );

        cartItemsRepository.delete(cartHelper.findCartItem(cartItemId));

        log.info("Cart item deleted: id={}", cartItemId);
    }
}
