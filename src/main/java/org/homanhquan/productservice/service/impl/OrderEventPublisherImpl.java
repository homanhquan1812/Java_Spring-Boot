package org.homanhquan.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.homanhquan.productservice.dto.order.event.OrderCreatedEvent;
import org.homanhquan.productservice.service.OrderEventPublisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static org.homanhquan.productservice.common.constants.MessageQueueConstants.ORDER_EXCHANGE;
import static org.homanhquan.productservice.common.constants.MessageQueueConstants.ORDER_ROUTING_KEY;

@Service
@RequiredArgsConstructor
public class OrderEventPublisherImpl implements OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishOrderCreated(OrderCreatedEvent event) {
        rabbitTemplate.convertAndSend(
                ORDER_EXCHANGE,
                ORDER_ROUTING_KEY,
                event
        );
    }
}
