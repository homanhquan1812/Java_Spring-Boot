package org.homanhquan.productservice.dto.orders.request;

import org.homanhquan.productservice.enums.Status;

public record UpdateOrderStatusRequest(
        Status status
) {
}
