package org.homanhquan.productservice.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface CartItemProjection {
    String getName();
    BigDecimal getPrice();
    Integer getQuantity();
}
