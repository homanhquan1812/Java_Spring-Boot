package org.homanhquan.productservice.event.order;

import java.util.List;
import java.util.UUID;

public record OrderCreatedSpringEvent(
        UUID orderId,
        UUID userId,
        List<CartItemSnapshot> cartItems
) {}