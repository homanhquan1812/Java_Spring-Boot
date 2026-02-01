package org.homanhquan.authservice.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface CartItemsProjection {
    String getName();
    BigDecimal getPrice();
    Integer getQuantity();
    LocalDateTime getUpdatedAt();
}
