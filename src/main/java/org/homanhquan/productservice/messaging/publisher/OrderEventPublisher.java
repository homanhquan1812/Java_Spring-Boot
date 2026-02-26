package org.homanhquan.productservice.messaging.publisher;

import lombok.RequiredArgsConstructor;
import org.homanhquan.productservice.event.order.OrderCreatedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static org.homanhquan.productservice.common.constants.MessageQueueConstants.ORDER_EXCHANGE;
import static org.homanhquan.productservice.common.constants.MessageQueueConstants.ORDER_ROUTING_KEY;

@Service
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishOrderCreated(OrderCreatedEvent event) {
        rabbitTemplate.convertAndSend(
                ORDER_EXCHANGE,
                ORDER_ROUTING_KEY,
                event
        );
    }
}
