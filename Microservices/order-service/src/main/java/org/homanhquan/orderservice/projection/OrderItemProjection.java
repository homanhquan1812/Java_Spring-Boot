package org.homanhquan.orderservice.projection;

import java.math.BigDecimal;

public interface OrderItemProjection {
    String getName();
    BigDecimal getPrice();
    Integer getQuantity();
}
