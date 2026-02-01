package org.homanhquan.productservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.homanhquan.productservice.dto.orderItem.response.OrderItemsResponse;
import org.homanhquan.productservice.dto.order.request.CreateOrderRequest;
import org.homanhquan.productservice.dto.order.request.UpdateOrderStatusRequest;
import org.homanhquan.productservice.dto.order.response.OrderResponse;
import org.homanhquan.productservice.service.OrderService;
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
    public ResponseEntity<List<OrderResponse>> getAllOrders(
            @AuthenticationPrincipal(expression = "id") UUID userId
    ) {
        return ResponseEntity.ok(orderService.getAllOrders(userId));
    }

    /*
    @GetMapping("/my-list")
    public ResponseEntity<List<OrdersResponse>> getAllOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID userId = userDetails.getId();
        String username = userDetails.getUsername();
        Role role = userDetails.getRole();

        return ResponseEntity.ok(ordersService.getAllOrders(userId));
    }
     */

    // [GET] /api/order/my-list/{orderId}
    @GetMapping("/my-list/{orderId}")
    public ResponseEntity<List<OrderItemsResponse>> getSpecificOrder(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable UUID orderId
    ) {
        return ResponseEntity.ok(orderService.getSpecificOrder(userId, orderId));
    }

    // [POST] /api/order/submit
    @PostMapping("/submit")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<OrderResponse> createOrder(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @Valid @RequestBody CreateOrderRequest createOrderRequest) {
        return ResponseEntity.ok(orderService.createOrder(userId, createOrderRequest));
    }

    ///////////////////// STAFF /////////////////////

    // [GET] /api/order/all
    @GetMapping("/all")
    public ResponseEntity<List<OrderResponse>> getOrdersFromAllUsers(
            @AuthenticationPrincipal(expression = "id") UUID userId
    ) {
        return ResponseEntity.ok(orderService.getOrdersFromAllUsers(userId));
    }

    // [PATCH] /api/order/{orderId}/status
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable UUID orderId,
            @Valid @RequestBody UpdateOrderStatusRequest updateOrderStatusRequest) {
        return ResponseEntity.ok(orderService.updateOrderStatus(userId, orderId, updateOrderStatusRequest));
    }
}

