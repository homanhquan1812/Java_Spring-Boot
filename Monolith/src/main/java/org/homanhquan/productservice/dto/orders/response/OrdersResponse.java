package org.homanhquan.productservice.dto.orders.response;

import lombok.Builder;
import org.homanhquan.productservice.enums.PaymentMethod;
import org.homanhquan.productservice.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record OrdersResponse(
        UUID id,
        UUID userId,
        BigDecimal totalPrice,
        Status status,
        PaymentMethod paymentMethod,
        LocalDateTime createdAt
) {
}
