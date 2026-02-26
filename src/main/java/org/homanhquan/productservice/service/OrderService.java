package org.homanhquan.productservice.service;

import org.homanhquan.productservice.dto.common.PageResponse;
import org.homanhquan.productservice.dto.orderItem.response.OrderItemResponse;
import org.homanhquan.productservice.dto.order.request.UpdateOrderStatusRequest;
import org.homanhquan.productservice.dto.order.response.OrderResponse;
import org.homanhquan.productservice.enums.Role;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    PageResponse<OrderResponse> getPage(Pageable pageable, UUID userId);
    List<OrderItemResponse> getById(UUID userId, Role role, UUID orderId);
    OrderResponse updateStatus(UUID userId, UUID orderId, UpdateOrderStatusRequest updateOrderStatusRequest);
}