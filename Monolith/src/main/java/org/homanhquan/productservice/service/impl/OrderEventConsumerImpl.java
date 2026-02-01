package org.homanhquan.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.productservice.config.RabbitMQConfig;
import org.homanhquan.productservice.dto.order.event.OrderCreatedEvent;
import org.homanhquan.productservice.entity.Cart;
import org.homanhquan.productservice.entity.Order;
import org.homanhquan.productservice.entity.OrderItem;
import org.homanhquan.productservice.enums.Status;
import org.homanhquan.productservice.exception.ResourceNotFoundException;
import org.homanhquan.productservice.repository.CartItemsRepository;
import org.homanhquan.productservice.repository.CartRepository;
import org.homanhquan.productservice.repository.OrderItemRepository;
import org.homanhquan.productservice.repository.OrderRepository;
import org.homanhquan.productservice.service.OrderEventConsumer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderEventConsumerImpl implements OrderEventConsumer {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemsRepository cartItemsRepository;

    @RabbitListener(queues = RabbitMQConfig.ORDER_QUEUE)
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Received order event for orderId: {}", event.orderId());

        try {
            // 1. Save OrderItems từ snapshot
            List<OrderItem> orderItems = event.cartItems().stream()
                    .map(item -> {
                        OrderItem orderItem = new OrderItem();
                        orderItem.setOrderId(event.orderId());
                        orderItem.setProductId(item.productId());
                        orderItem.setName(item.name());
                        orderItem.setPrice(item.price());
                        orderItem.setQuantity(item.quantity());
                        return orderItem;
                    })
                    .toList();

            orderItemRepository.saveAll(orderItems);

            // 2. Xóa cart items
            Cart cart = cartRepository.findByUserId(event.userId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
            cartItemsRepository.deleteByCartId(cart.getId());

            // 3. Update order status → PENDING
            Order order = orderRepository.findById(event.orderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
            order.setStatus(Status.PENDING);
            orderRepository.save(order);

            log.info("Order {} processed successfully", event.orderId());

        } catch (Exception e) {
            log.error("Failed to process order {}: {}", event.orderId(), e.getMessage());
            throw e; // Re-throw → RabbitMQ sẽ retry (nếu cấu hình)
        }
    }
}
