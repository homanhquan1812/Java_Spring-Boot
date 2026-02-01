package org.homanhquan.productservice.dto.orders.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.homanhquan.productservice.enums.PaymentMethod;

@Builder
public record CreateOrdersRequest(
        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod
) {
}