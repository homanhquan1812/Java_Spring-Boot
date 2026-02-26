package org.homanhquan.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.productservice.dto.cart.response.CartResponse;
import org.homanhquan.productservice.dto.cartItem.request.CreateCartItemsRequest;
import org.homanhquan.productservice.dto.cartItem.response.CartItemResponse;
import org.homanhquan.productservice.dto.order.request.CheckoutRequest;
import org.homanhquan.productservice.dto.order.response.OrderResponse;
import org.homanhquan.productservice.entity.Cart;
import org.homanhquan.productservice.entity.CartItem;
import org.homanhquan.productservice.entity.Order;
import org.homanhquan.productservice.exception.ResourceNotFoundException;
import org.homanhquan.productservice.mapper.CartItemMapper;
import org.homanhquan.productservice.mapper.OrderMapper;
import org.homanhquan.productservice.projection.CartItemProjection;
import org.homanhquan.productservice.repository.CartItemRepository;
import org.homanhquan.productservice.repository.CartRepository;
import org.homanhquan.productservice.service.CartService;
import org.homanhquan.productservice.service.helper.order.OrderCreationHelper;
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
    private final CartItemMapper cartItemMapper;
    private final CartItemRepository cartItemRepository;
    private final OrderCreationHelper orderCreationHelper;
    private final OrderMapper orderMapper;

    private Cart findCartByUserId(UUID userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with id: " + userId));
    }

    private CartItem findCartItem(UUID cartItemId) {
        return cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));
    }

    private List<CartItem> validateCartItemsInCart(UUID cartId) {
        List<CartItem> items = cartItemRepository.findByCartId(cartId);

        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot create order from empty cart");
        }

        return items;
    }

    @Override
    public CartResponse get(UUID userId) {
        List<CartItemProjection> cartItemProjections = cartRepository.findCartWithSpecificItemsById(userId);

        BigDecimal totalPrice = cartItemProjections.stream()
                .map(item -> item.getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .items(cartItemMapper.projectionToDtoList(cartItemProjections))
                .totalPrice(totalPrice)
                .build();
    }

    /**
     * Creates a new order from the user's cart items.
     * Flow:
     * 1. Validate and retrieve the user's cart, then validate cart items (ensuring cart is not empty).
     * 2. Calculate the total price from all cart items.
     * 3. Create and save the order with PENDING status.
     * 4. Publish an order created event to RabbitMQ for asynchronous processing
     *    (OrderItems creation and cart cleanup will be handled by the event consumer).
     *
     * @param userId
     * @param request
     * @return
     */
    @Override
    @Transactional
    public OrderResponse create(UUID userId, CheckoutRequest request) {
        Cart cart = findCartByUserId(userId);
        List<CartItem> cartItems = validateCartItemsInCart(cart.getId());

        BigDecimal totalPrice = orderCreationHelper.calculateTotalPrice(cartItems);

        Order savedOrder = orderCreationHelper.createAndSaveOrder(userId, totalPrice, request);

        orderCreationHelper.publishOrderCreatedEvent(savedOrder, userId, cartItems);

        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public CartItemResponse addItem(UUID userId, CreateCartItemsRequest request) {
        CartItem cartItem = cartItemMapper.toEntity(request);

        Cart cart = findCartByUserId(userId);
        cartItem.setCartId(cart.getId());

        CartItem savedCartItem = cartItemRepository.save(cartItem);

        log.info("Cart item {} added into cart successfully by {}.",
                savedCartItem.getId(),
                userId
        );

        return cartItemMapper.toDto(savedCartItem);
    }

    @Override
    @Transactional
    public void deleteByCartItemId(UUID userId, UUID cartItemId) {
        log.warn("WARNING: User {} is deleting cart item {}!",
                userId,
                cartItemId
        );

        cartItemRepository.delete(findCartItem(cartItemId));

        log.info("Cart item deleted: id={}", cartItemId);
    }
}
