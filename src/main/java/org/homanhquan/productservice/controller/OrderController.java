package org.homanhquan.productservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.homanhquan.productservice.annotation.swagger.crud.*;
import org.homanhquan.productservice.dto.common.PageResponse;
import org.homanhquan.productservice.dto.orderItem.response.OrderItemResponse;
import org.homanhquan.productservice.dto.order.request.UpdateOrderStatusRequest;
import org.homanhquan.productservice.dto.order.response.OrderResponse;
import org.homanhquan.productservice.enums.Role;
import org.homanhquan.productservice.security.userDetails.CustomUserDetails;
import org.homanhquan.productservice.service.OrderService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static org.homanhquan.productservice.common.constants.ProductSortConstants.*;
import static org.homanhquan.productservice.common.constants.ProductSortConstants.DEFAULT_SORT_DIRECTION;
import static org.homanhquan.productservice.common.constants.ProductSortConstants.DEFAULT_SORT_FIELD;

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
@Tag(name = "Order", description = "Order management APIs")
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // [GET] /api/order/all
    @Operation(summary = "Users get their orders with pagination")
    @GetAllApiResponse
    @AuthApiResponse
    @GetMapping("/all")
    public PageResponse<OrderResponse> getPage(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @RequestParam(defaultValue = "0") @Min(MIN_PAGE) int page,
            @RequestParam(defaultValue = "10") @Min(MIN_SIZE) @Max(MAX_SIZE) int size,
            @RequestParam(defaultValue = DEFAULT_SORT_FIELD) String sort,
            @RequestParam(defaultValue = DEFAULT_SORT_DIRECTION)
            @Pattern(regexp = "^(asc|desc)$", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Direction must be asc|desc|ASC|DESC")
            String direction) {
        /* Map (Mostly used with nativeQuery in Repository):
        String sortField = ALLOWED_SORT_FIELDS.getOrDefault(sort, "createdAt");
         */
        // Set:
        String sortField = ALLOWED_SORT_FIELDS.contains(sort)
                ? sort
                : DEFAULT_SORT_FIELD;

        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField));

        return orderService.getPage(pageable, userId);
    }

    // [GET] /api/order/{orderId}
    @Operation(summary = "Users get a list of order items from order ID")
    @GetByIdApiResponse
    @AuthApiResponse
    @GetMapping("/{orderId}")
    public List<OrderItemResponse> getById(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable UUID orderId
    ) {
        Role role = customUserDetails.getRole();
        UUID userId = customUserDetails.getId();

        return orderService.getById(userId, role, orderId);
    }

    // [PATCH] /api/order/{orderId}/status
    @Operation(summary = "Staffs update order's status")
    @PutAndPatchApiResponse
    @AuthApiResponse
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable UUID orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(orderService.updateStatus(userId, orderId, request));
    }
}

