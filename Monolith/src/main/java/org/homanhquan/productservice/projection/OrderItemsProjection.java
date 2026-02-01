package org.homanhquan.productservice.projection;

import java.math.BigDecimal;

public interface OrderItemsProjection {
    String getName();
    BigDecimal getPrice();
    Integer getQuantity();
}
