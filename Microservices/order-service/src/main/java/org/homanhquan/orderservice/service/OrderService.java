package org.homanhquan.orderservice.service;

import org.homanhquan.orderservice.dto.orderItem.response.OrderItemResponse;
import org.homanhquan.orderservice.dto.order.request.CreateOrderRequest;
import org.homanhquan.orderservice.dto.order.request.UpdateOrderStatusRequest;
import org.homanhquan.orderservice.dto.order.response.OrderResponse;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    ///////////////////// USER /////////////////////
    List<OrderResponse> getAllOrders(UUID userId);
    List<OrderItemResponse> getSpecificOrder(UUID userId, UUID orderId);
    OrderResponse createOrder(UUID userId, CreateOrderRequest createOrderRequest);

    ///////////////////// STAFF /////////////////////
    List<OrderResponse> getOrdersFromAllUsers(UUID staffId);
    OrderResponse updateOrderStatus(UUID staffId, UUID orderId, UpdateOrderStatusRequest updateOrderStatusRequest);
}