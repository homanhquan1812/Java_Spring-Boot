package org.homanhquan.productservice.service.helper.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.productservice.event.order.CartItemSnapshot;
import org.homanhquan.productservice.event.order.OrderCreatedEvent;
import org.homanhquan.productservice.dto.order.request.CheckoutRequest;
import org.homanhquan.productservice.entity.CartItem;
import org.homanhquan.productservice.entity.Order;
import org.homanhquan.productservice.enums.Status;
import org.homanhquan.productservice.event.order.OrderCreatedSpringEvent;
import org.homanhquan.productservice.repository.OrderRepository;
import org.homanhquan.productservice.messaging.publisher.OrderEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreationHelper {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;
    private final ApplicationEventPublisher applicationEventPublisher;

    public BigDecimal calculateTotalPrice(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Order createAndSaveOrder(UUID userId, BigDecimal totalPrice, CheckoutRequest request) {
        Order order = Order.of(
                userId,
                totalPrice,
                Status.PENDING,
                request.paymentMethod()
        );

        return orderRepository.save(order);
    }

    public void publishOrderCreatedEvent(Order order, UUID userId, List<CartItem> cartItems) {
        List<CartItemSnapshot> snapshots = createSnapshots(cartItems);

        applicationEventPublisher.publishEvent(
                new OrderCreatedSpringEvent(order.getId(), userId, snapshots)
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