package org.homanhquan.productservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.homanhquan.productservice.annotation.swagger.crud.DeleteApiResponse;
import org.homanhquan.productservice.annotation.swagger.crud.PostApiResponse;
import org.homanhquan.productservice.annotation.swagger.crud.PutAndPatchApiResponse;
import org.homanhquan.productservice.annotation.swagger.crud.GetByIdApiResponse;
import org.homanhquan.productservice.annotation.swagger.crud.GetAllApiResponse;
import org.homanhquan.productservice.dto.common.PageResponse;
import org.homanhquan.productservice.dto.product.request.CreateProductRequest;
import org.homanhquan.productservice.dto.product.request.UpdateProductRequest;
import org.homanhquan.productservice.dto.product.request.UpdateProductStatusRequest;
import org.homanhquan.productservice.dto.product.response.ProductResponse;
import org.homanhquan.productservice.service.ProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.homanhquan.productservice.common.constants.ProductSortConstants.ALLOWED_SORT_FIELDS;
import static org.homanhquan.productservice.common.constants.ProductSortConstants.DEFAULT_SORT_DIRECTION;
import static org.homanhquan.productservice.common.constants.ProductSortConstants.DEFAULT_SORT_FIELD;
import static org.homanhquan.productservice.common.constants.ProductSortConstants.MAX_SIZE;
import static org.homanhquan.productservice.common.constants.ProductSortConstants.MIN_PAGE;
import static org.homanhquan.productservice.common.constants.ProductSortConstants.MIN_SIZE;

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
 * - PUT replaces the entire resource (full update).
 * - PATCH updates only specified fields (partial update).
 * - Performance difference is not significant.
 * - PATCH is more commonly used since most updates are partial.
 */
@Tag(name = "Product", description = "Product management APIs")
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final ProductService productService;

    /**
     * - Avoid exposing non-paginated list endpoints for large datasets.
     * - Use pagination to prevent performance and memory issues.
     */
    // [GET] /api/product/list
    @Deprecated(since = "1.0", forRemoval = true)
    @Operation(
            summary = "Get a list of products (limited to 100)",
            deprecated = true,
            description = "This endpoint is deprecated. Use [GET] /api/product with pagination instead."
    )
    @GetAllApiResponse
    @GetMapping("/list")
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts()
                .stream()
                .limit(100)
                .toList();
    }

    // [GET] /api/product?...=...&...=...
    @Operation(summary = "Get products with pagination")
    @GetAllApiResponse
    @GetMapping
    public PageResponse<ProductResponse> getProductsPage(
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

        return productService.getProductsPage(pageable);
    }

    // [GET] /api/product/{productId}
    @Operation(summary = "Get product by ID")
    @GetByIdApiResponse
    @GetMapping("/{productId}")
    public ProductResponse getProductById(@PathVariable @Min(1) Long productId) {
        return productService.getProductById(productId);
    }

    // [POST] /api/product
    @PostMapping
    @Operation(summary = "Create new product")
    @PostApiResponse
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @Valid @RequestBody CreateProductRequest createProductRequest
    ) {
        return productService.createProduct(userId, createProductRequest);
    }

    // [PATCH] /api/product/{productId}
    @Operation(summary = "Update product by ID")
    @PutAndPatchApiResponse
    @PatchMapping("/{productId}")
    public ProductResponse updateProduct(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable @Min(1) Long productId,
            @Valid @RequestBody UpdateProductRequest updateProductRequest) {
        return productService.updateProduct(userId, productId, updateProductRequest);
    }

    // [PATCH] /api/product/{productId}/status
    @Operation(summary = "Update product status (soft-delete) by ID")
    @PutAndPatchApiResponse
    @PatchMapping("/{productId}/status")
    public ProductResponse updateProductStatus(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable @Min(1) Long productId,
            @Valid @RequestBody UpdateProductStatusRequest updateProductStatusRequest) {
        return productService.updateProductStatus(userId, productId, updateProductStatusRequest);
    }

    // [DELETE] /api/product/{productId}
    @Operation(summary = "Delete product permanently by ID")
    @DeleteApiResponse
    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable @Min(1) Long productId
    ) {
        productService.deleteProduct(userId, productId);
    }
}
