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
import org.homanhquan.productservice.dto.cartItems.request.CreateCartItemsRequest;
import org.homanhquan.productservice.dto.cartItems.response.CartItemsResponse;
import org.homanhquan.productservice.security.CustomUserDetails;
import org.homanhquan.productservice.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Spring Boot uses Jackson to convert data between Java objects and JSON in controllers. Specifically:
 * - Serialization: Java object → JSON.
 * - Deserialization: JSON → Java object.
 * ==================================================
 * Annotation definition:
 * - @Tag: Groups related API endpoints in Swagger documentation.
 * - @RequiredArgsConstructor: Automatically injects dependencies for constructor.
 * - @Validated: Enables validation of method parameters (e.g., @Min, @Max) at the controller level.
 * - @Controller: A bean for the web layer (MVC) to return views (like JSP, Thymeleaf templates).
 * - @RestController: A specialized @Controller that combines @Controller and @ResponseBody, automatically serializing return values to JSON/XML for REST APIs.
 * - @Controller: A bean for the web layer (MVC) to return views (like JSP, Thymeleaf templates).
 * - @RequestMapping("/api"): Defines base URL starting with /api.
 * - @PathVariable: Extract path variables.
 * - @RequestBody: Convert JSON to object.
 * - @RequestParam: Extract query parameters.
 * - ResponseEntity<T>: Represents HTTP response with body, status, and headers. No need to use it when:
 *   + The HTTP status is fixed (e.g. 200 OK, 201 CREATED).
 *   + No custom HTTP headers are required.
 *   + Errors are handled centrally using @ControllerAdvice.
 * - @Valid: Triggers validation on request body or method parameters using Bean Validation constraints (e.g. @NotNull, @Size).
 * - @AuthenticationPrincipal(expression = "id") UUID userId: Resolves the current user ID from authentication. Useful for logging, or avoiding /users/{id} pattern.
 *   If additional user information is required, you can inject the full principal: @AuthenticationPrincipal CustomUserDetails userDetails.
 *   Then extract fields as needed, for example:
 *   + UUID userId = userDetails.getId();
 *   + String username = userDetails.getUsername();
 *   In most cases, using @AuthenticationPrincipal(expression = "id") is sufficient.
 * - @Deprecated(since = "1.0", forRemoval = true): This method will be removed in the future since version 1.0.
 * ==================================================
 * Difference between PUT vs PATCH:
 * - Not much difference between performance and syntax.
 * - They still need NullValuePropertyMappingStrategy.IGNORE in Mapper to keep old data if no update for some fields.
 * - Since a lot of methods just update partial data, PATCH is more common than PUT.
 */
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
    public CartResponse getCartItems(
            @AuthenticationPrincipal(expression = "id") UUID userId
    ) {
        return cartService.getCartItems(userId);
    }

    // POST /api/cart/add-product
    @Operation(summary = "Users add chosen products in their cart")
    @PostMapping("/add-product")
    @PostApiResponse
    @AuthApiResponse
    @ResponseStatus(HttpStatus.CREATED)
    public CartItemsResponse createCartItems(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @Valid @RequestBody CreateCartItemsRequest createCartItemsRequest) {
        return cartService.createCartItems(userId, createCartItemsRequest);
    }

    // DELETE /api/cart/{cartItemId}
    @Operation(summary = "Users remove chosen products from their cart")
    @DeleteMapping("/{cartItemId}")
    @DeleteApiResponse
    @AuthApiResponse
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCartItems(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable UUID cartItemId
    ) {
        cartService.deleteCartItems(userId, cartItemId);
    }
}
