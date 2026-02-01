package org.homanhquan.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.homanhquan.productservice.config.RabbitMQConfig;
import org.homanhquan.productservice.dto.order.event.OrderCreatedEvent;
import org.homanhquan.productservice.service.OrderEventPublisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventPublisherImpl implements OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishOrderCreated(OrderCreatedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.ORDER_ROUTING_KEY,
                event
        );
    }
}
