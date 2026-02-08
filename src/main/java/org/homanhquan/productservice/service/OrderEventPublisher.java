package org.homanhquan.productservice.service;

import org.homanhquan.productservice.dto.order.event.OrderCreatedEvent;

public interface OrderEventPublisher {

    void publishOrderCreated(OrderCreatedEvent event);
}
