package org.homanhquan.productservice.dto.order.request;

import org.homanhquan.productservice.enums.Status;

public record UpdateOrderStatusRequest(
        Status status
) {
}
