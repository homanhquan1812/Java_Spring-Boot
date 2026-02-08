package org.homanhquan.productservice.dto.order.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.homanhquan.productservice.enums.PaymentMethod;

@Builder
public record CreateOrderRequest(
        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod
) {
}