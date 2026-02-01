package org.homanhquan.orderservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.homanhquan.orderservice.dto.orderItem.response.OrderItemResponse;
import org.homanhquan.orderservice.dto.order.request.CreateOrderRequest;
import org.homanhquan.orderservice.dto.order.request.UpdateOrderStatusRequest;
import org.homanhquan.orderservice.dto.order.response.OrderResponse;
import org.homanhquan.orderservice.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    ///////////////////// USER /////////////////////

    // [GET] /api/order/my-list
    @GetMapping("/my-list")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<OrderResponse>> getAllOrders(
            @AuthenticationPrincipal(expression = "id") UUID userId
    ) {
        return ResponseEntity.ok(orderService.getAllOrders(userId));
    }

    // [GET] /api/order/my-list/{orderId}
    @GetMapping("/my-list/{orderId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<OrderItemResponse>> getSpecificOrder(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable UUID orderId
    ) {
        return ResponseEntity.ok(orderService.getSpecificOrder(userId, orderId));
    }

    // [POST] /api/order/submit
    @PostMapping("/submit")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<OrderResponse> createOrder(
            @AuthenticationPrincipal(expression = "id") UUID userId, // JWT
            @Valid @RequestBody CreateOrderRequest createOrderRequest) {
        return ResponseEntity.ok(orderService.createOrder(userId, createOrderRequest));
    }

    ///////////////////// STAFF /////////////////////

    // [GET] /api/order/all
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('CHEF', 'WAITER')")
    public ResponseEntity<List<OrderResponse>> getOrdersFromAllUsers(
            @AuthenticationPrincipal(expression = "id") UUID staffId // JWT
    ) {
        return ResponseEntity.ok(orderService.getOrdersFromAllUsers(staffId));
    }

    // [PUT] /api/order/{orderId}/status
    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasAnyRole('CHEF', 'WAITER')")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @AuthenticationPrincipal(expression = "id") UUID staffId, // JWT
            @PathVariable UUID orderId,
            @Valid @RequestBody UpdateOrderStatusRequest updateOrderStatusRequest) {
        return ResponseEntity.ok(orderService.updateOrderStatus(staffId, orderId, updateOrderStatusRequest));
    }
}

