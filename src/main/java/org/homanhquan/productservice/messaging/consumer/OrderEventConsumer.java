package org.homanhquan.productservice.messaging.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.productservice.event.order.OrderCreatedEvent;
import org.homanhquan.productservice.entity.*;
import org.homanhquan.productservice.service.helper.order.OrderEventConsumerHelper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.homanhquan.productservice.common.constants.MessageQueueConstants.ORDER_QUEUE;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderEventConsumer {

    private final OrderEventConsumerHelper consumerHelper;

    /**
     * Handles order created events from RabbitMQ.
     *
     * This consumer processes the order asynchronously by:
     * 1. Creating order items from the cart item snapshots
     * 2. Clearing the user's cart
     * 3. Updating the order status to PENDING
     * 4. Sending order confirmation email
     *
     * If any step fails, RabbitMQ will retry based on the configured retry policy.
     *
     * @param event the order created event containing order and cart details
     */
    @RabbitListener(queues = ORDER_QUEUE)
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Received order event for orderId: {}", event.orderId());

        consumerHelper.saveOrderItems(event.orderId(), event.cartItems());
        consumerHelper.clearUserCart(event.userId());
        Order savedOrder = consumerHelper.updateOrderStatus(event.orderId());
        consumerHelper.sendOrderConfirmationEmail(event.userId(), savedOrder);
    }
}
