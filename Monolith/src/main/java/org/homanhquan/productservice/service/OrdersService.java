package org.homanhquan.productservice.service;

import org.homanhquan.productservice.dto.orderItems.response.OrderItemsResponse;
import org.homanhquan.productservice.dto.orders.request.CreateOrdersRequest;
import org.homanhquan.productservice.dto.orders.request.UpdateOrderStatusRequest;
import org.homanhquan.productservice.dto.orders.response.OrdersResponse;

import java.util.List;
import java.util.UUID;

public interface OrdersService {

    ///////////////////// USER /////////////////////
    List<OrdersResponse> getAllOrders(UUID userId);
    List<OrderItemsResponse> getSpecificOrder(UUID userId, UUID orderId);
    OrdersResponse createOrder(UUID userId, CreateOrdersRequest createOrdersRequest);

    ///////////////////// STAFF /////////////////////
    List<OrdersResponse> getOrdersFromAllUsers(UUID userId);
    OrdersResponse updateOrderStatus(UUID userId, UUID orderId, UpdateOrderStatusRequest updateOrderStatusRequest);
}