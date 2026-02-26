package org.homanhquan.productservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.homanhquan.productservice.annotation.swagger.crud.AuthApiResponse;
import org.homanhquan.productservice.annotation.swagger.crud.DeleteApiResponse;
import org.homanhquan.productservice.annotation.swagger.crud.GetAllApiResponse;
import org.homanhquan.productservice.annotation.swagger.crud.PostApiResponse;
import org.homanhquan.productservice.dto.cart.response.CartResponse;
import org.homanhquan.productservice.dto.cartItem.request.CreateCartItemsRequest;
import org.homanhquan.productservice.dto.cartItem.response.CartItemResponse;
import org.homanhquan.productservice.dto.order.request.CheckoutRequest;
import org.homanhquan.productservice.dto.order.response.OrderResponse;
import org.homanhquan.productservice.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Cart", description = "Cart management APIs")
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // [GET] /api/cart
    @Operation(summary = "Users get all chosen products from their cart")
    @GetAllApiResponse
    @AuthApiResponse
    @GetMapping
    public CartResponse get(
            @AuthenticationPrincipal(expression = "id") UUID userId
    ) {
        return cartService.get(userId);
    }

    // [POST] /api/cart
    @Operation(summary = "Users submit an order to the system")
    @PostApiResponse
    @AuthApiResponse
    @PostMapping
    public ResponseEntity<OrderResponse> checkout(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @Valid @RequestBody CheckoutRequest request) {
        return ResponseEntity.ok(cartService.create(userId, request));
    }

    // POST /api/cart/item/add
    @Operation(summary = "Users add chosen products in their cart")
    @PostMapping("/item/add")
    @PostApiResponse
    @AuthApiResponse
    @ResponseStatus(HttpStatus.CREATED)
    public CartItemResponse create(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @Valid @RequestBody CreateCartItemsRequest request) {
        return cartService.addItem(userId, request);
    }

    // DELETE /api/cart/item/{cartItemId}
    @Operation(summary = "Users remove chosen products from their cart")
    @DeleteMapping("/item/{cartItemId}")
    @DeleteApiResponse
    @AuthApiResponse
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable UUID cartItemId
    ) {
        cartService.deleteByCartItemId(userId, cartItemId);
    }
}
