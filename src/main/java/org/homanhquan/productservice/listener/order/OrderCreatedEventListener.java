package org.homanhquan.productservice.listener.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.productservice.event.order.OrderCreatedEvent;
import org.homanhquan.productservice.event.order.OrderCreatedSpringEvent;
import org.homanhquan.productservice.messaging.publisher.OrderEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCreatedEventListener {

    private final OrderEventPublisher orderEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderCreated(OrderCreatedSpringEvent event) {
        orderEventPublisher.publishOrderCreated(
                new OrderCreatedEvent(event.orderId(), event.userId(), event.cartItems())
        );

        log.info("Sending to RabbitMQ orderId: {}", event.orderId());
    }
}
