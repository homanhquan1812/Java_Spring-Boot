package org.homanhquan.productservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.homanhquan.productservice.dto.cart.response.CartResponse;
import org.homanhquan.productservice.dto.cartItems.request.CreateCartItemsRequest;
import org.homanhquan.productservice.dto.cartItems.response.CartItemsResponse;
import org.homanhquan.productservice.security.CustomUserDetails;
import org.homanhquan.productservice.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // [GET] /api/cart
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartResponse> getProductsInCart(
            @AuthenticationPrincipal(expression = "id") UUID userId
    ) {
        return ResponseEntity.ok(cartService.getProductsInCart(userId));
    }

    // POST /api/cart/add-product
    @PostMapping("/add-product")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartItemsResponse> createCartItems(
            @AuthenticationPrincipal CustomUserDetails userDetails, // JWT
            @Valid @RequestBody CreateCartItemsRequest createCartItemsRequest) {
        UUID userId = userDetails.getId();
        UUID cartId = userDetails.getCartId();

        CartItemsResponse cartItemsResponse = cartService.createCartItems(userId, cartId, createCartItemsRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemsResponse);
    }

    // DELETE /api/cart/{cartItemId}
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> deleteCartItems(
            @AuthenticationPrincipal(expression = "id") UUID userId, // JWT
            @PathVariable UUID cartItemId
    ) {
        cartService.deleteCartItems(userId, cartItemId);
        return ResponseEntity.noContent().build();
    }
}
