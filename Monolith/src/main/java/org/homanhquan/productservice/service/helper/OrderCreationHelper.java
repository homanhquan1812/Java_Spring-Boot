package org.homanhquan.productservice.service.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.productservice.dto.order.event.CartItemSnapshot;
import org.homanhquan.productservice.dto.order.event.OrderCreatedEvent;
import org.homanhquan.productservice.dto.order.request.CreateOrderRequest;
import org.homanhquan.productservice.entity.Cart;
import org.homanhquan.productservice.entity.CartItem;
import org.homanhquan.productservice.entity.Order;
import org.homanhquan.productservice.enums.Status;
import org.homanhquan.productservice.exception.ResourceNotFoundException;
import org.homanhquan.productservice.repository.CartItemsRepository;
import org.homanhquan.productservice.repository.CartRepository;
import org.homanhquan.productservice.repository.OrderRepository;
import org.homanhquan.productservice.service.impl.OrderEventPublisherImpl;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreationHelper {

    private final CartRepository cartRepository;
    private final CartItemsRepository cartItemsRepository;
    private final OrderRepository orderRepository;
    private final OrderEventPublisherImpl orderEventPublisher;

    public Cart validateAndGetCart(UUID userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user"));
    }

    public List<CartItem> validateCartItems(UUID cartId) {
        List<CartItem> items = cartItemsRepository.findByCartId(cartId);
        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot create order from empty cart");
        }
        return items;
    }

    public BigDecimal calculateTotalPrice(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Order createAndSaveOrder(UUID userId, BigDecimal totalPrice, CreateOrderRequest createOrderRequest) {
        Order order = new Order();
        order.setUserId(userId);
        order.setTotalPrice(totalPrice);
        order.setStatus(Status.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setPaymentMethod(createOrderRequest.paymentMethod());

        return orderRepository.save(order);
    }

    public void publishOrderCreatedEvent(Order order, UUID userId, List<CartItem> cartItems) {
        List<CartItemSnapshot> snapshots = createSnapshots(cartItems);

        orderEventPublisher.publishOrderCreated(
                new OrderCreatedEvent(order.getId(), userId, snapshots)
        );

        log.info("Published order created event for orderId: {}", order.getId());
    }

    private List<CartItemSnapshot> createSnapshots(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(item -> new CartItemSnapshot(
                        item.getProductId(),
                        item.getName(),
                        item.getPrice(),
                        item.getQuantity()
                ))
                .toList();
    }
}