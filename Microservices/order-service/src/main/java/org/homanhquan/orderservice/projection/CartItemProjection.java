package org.homanhquan.orderservice.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface CartItemProjection {
    String getName();
    BigDecimal getPrice();
    Integer getQuantity();
    LocalDateTime getUpdatedAt();
}

