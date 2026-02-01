package org.homanhquan.productservice.service;

import org.homanhquan.productservice.dto.orderItem.response.OrderItemsResponse;
import org.homanhquan.productservice.dto.order.request.CreateOrderRequest;
import org.homanhquan.productservice.dto.order.request.UpdateOrderStatusRequest;
import org.homanhquan.productservice.dto.order.response.OrderResponse;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    ///////////////////// USER /////////////////////
    List<OrderResponse> getAllOrders(UUID userId);
    List<OrderItemsResponse> getSpecificOrder(UUID userId, UUID orderId);
    OrderResponse createOrder(UUID userId, CreateOrderRequest createOrderRequest);

    ///////////////////// STAFF /////////////////////
    List<OrderResponse> getOrdersFromAllUsers(UUID userId);
    OrderResponse updateOrderStatus(UUID userId, UUID orderId, UpdateOrderStatusRequest updateOrderStatusRequest);
}