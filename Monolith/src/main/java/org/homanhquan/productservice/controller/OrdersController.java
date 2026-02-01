package org.homanhquan.productservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.homanhquan.productservice.dto.orderItems.response.OrderItemsResponse;
import org.homanhquan.productservice.dto.orders.request.CreateOrdersRequest;
import org.homanhquan.productservice.dto.orders.request.UpdateOrderStatusRequest;
import org.homanhquan.productservice.dto.orders.response.OrdersResponse;
import org.homanhquan.productservice.service.OrdersService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrdersController {

    private final OrdersService ordersService;

    ///////////////////// USER /////////////////////

    // [GET] /api/order/my-list
    @GetMapping("/my-list")
    public ResponseEntity<List<OrdersResponse>> getAllOrders(
            @AuthenticationPrincipal(expression = "id") UUID userId
    ) {
        return ResponseEntity.ok(ordersService.getAllOrders(userId));
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
        return ResponseEntity.ok(ordersService.getSpecificOrder(userId, orderId));
    }

    // [POST] /api/order/submit
    @PostMapping("/submit")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<OrdersResponse> createOrder(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @Valid @RequestBody CreateOrdersRequest createOrdersRequest) {
        return ResponseEntity.ok(ordersService.createOrder(userId, createOrdersRequest));
    }

    ///////////////////// STAFF /////////////////////

    // [GET] /api/order/all
    @GetMapping("/all")
    public ResponseEntity<List<OrdersResponse>> getOrdersFromAllUsers(
            @AuthenticationPrincipal(expression = "id") UUID userId
    ) {
        return ResponseEntity.ok(ordersService.getOrdersFromAllUsers(userId));
    }

    // [PATCH] /api/order/{orderId}/status
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrdersResponse> updateOrderStatus(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable UUID orderId,
            @Valid @RequestBody UpdateOrderStatusRequest updateOrderStatusRequest) {
        return ResponseEntity.ok(ordersService.updateOrderStatus(userId, orderId, updateOrderStatusRequest));
    }
}

