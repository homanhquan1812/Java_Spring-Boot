package org.homanhquan.productservice.dto.order.event;

import java.util.List;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID orderId,
        UUID userId,
        List<CartItemSnapshot> cartItems
) {}