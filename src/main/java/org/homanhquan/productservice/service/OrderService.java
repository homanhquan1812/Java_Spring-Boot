package org.homanhquan.productservice.service;

import org.homanhquan.productservice.dto.common.PageResponse;
import org.homanhquan.productservice.dto.orderItem.response.OrderItemResponse;
import org.homanhquan.productservice.dto.order.request.CreateOrderRequest;
import org.homanhquan.productservice.dto.order.request.UpdateOrderStatusRequest;
import org.homanhquan.productservice.dto.order.response.OrderResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    ///////////////////// USER /////////////////////
    PageResponse<OrderResponse> getAllOrders(Pageable pageable, UUID userId);
    List<OrderItemResponse> getSpecificOrder(UUID userId, UUID orderId);
    OrderResponse createOrder(UUID userId, CreateOrderRequest createOrderRequest);

    ///////////////////// STAFF /////////////////////
    //List<OrderResponse> getOrdersFromAllUsers(UUID userId);
    OrderResponse updateOrderStatus(UUID userId, UUID orderId, UpdateOrderStatusRequest updateOrderStatusRequest);
}