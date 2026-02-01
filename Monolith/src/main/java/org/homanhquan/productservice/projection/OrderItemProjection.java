package org.homanhquan.productservice.projection;

import java.math.BigDecimal;

public interface OrderItemProjection {
    String getName();
    BigDecimal getPrice();
    Integer getQuantity();
}
