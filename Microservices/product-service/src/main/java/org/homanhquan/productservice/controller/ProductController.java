package org.homanhquan.productservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static org.homanhquan.productservice.common.constants.ProductSortConstants.ALLOWED_SORT_FIELDS;
import static org.homanhquan.productservice.common.constants.ProductSortConstants.DEFAULT_SORT_DIRECTION;
import static org.homanhquan.productservice.common.constants.ProductSortConstants.DEFAULT_SORT_FIELD;
import static org.homanhquan.productservice.common.constants.ProductSortConstants.MAX_SIZE;
import static org.homanhquan.productservice.common.constants.ProductSortConstants.MIN_PAGE;
import static org.homanhquan.productservice.common.constants.ProductSortConstants.MIN_SIZE;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@Validated
public class ProductController {

    /**
     * Spring Boot uses Jackson to convert data between Java objects and JSON in controllers. Specifically:
     * - Serialization: Java object → JSON.
     * - Deserialization: JSON → Java object.
     * ==================================================
     * Annotation/Method definition:
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
     *   + The endpoint always returns 200 OK (Mostly GET, PATCH, PUT methods).
     *   + No custom HTTP headers are required.
     *   + Errors are handled centrally using @ControllerAdvice.
     * - @Valid: Triggers validation on request body or method parameters using Bean Validation constraints (e.g. @NotNull, @Size).
     * - @AuthenticationPrincipal(expression = "id") UUID userId: Resolves the current user ID from authentication, avoiding /users/{id}.
     * - @Deprecated(since = "1.0", forRemoval = true): This method will be removed in the future since version 1.0.
     */
    private final ProductService productService;

    /**
     * - Avoid exposing non-paginated list endpoints for large datasets.
     * - Use pagination to prevent performance and memory issues.
     */
    // [GET] /api/product/list
    @Deprecated(since = "1.0", forRemoval = true)
    @Operation(
            summary = "Get a list of products",
            deprecated = true,
            description = "This endpoint is deprecated. Use [GET] /api/product with pagination instead."
    )
    @ApiResponse(responseCode = "200", description = "Products retrieved (limited to 100)")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping("/list")
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts()
                .stream()
                .limit(100)
                .toList();
    }

    // [GET] /api/product?...=...&...=...
    @Operation(summary = "Get products with pagination")
    @ApiResponse(responseCode = "200", description = "Products retrieved")
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
    @ApiResponse(responseCode = "200", description = "Product found")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping("/{productId}")
    public ProductResponse getProductById(@PathVariable @Min(1) Long productId) {
        return productService.getProductById(productId);
    }

    // [POST] /api/product
    @PostMapping
    @Operation(summary = "Create new product")
    @ApiResponse(responseCode = "201", description = "Product created")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<ProductResponse> createProduct(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @Valid @RequestBody CreateProductRequest createProductRequest
    ) {
        ProductResponse createdProduct = productService.createProduct(userId, createProductRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    /**
     * Difference between PUT vs PATCH:
     * - Not much difference between performance and syntax.
     * - They still need NullValuePropertyMappingStrategy.IGNORE in Mapper to keep old data if no update for some fields.
     * - Since a lot of methods just update partial data, PATCH is more common than PUT.
     */
    // [PATCH] /api/product/{productId}
    @Operation(summary = "Update product by ID")
    @ApiResponse(responseCode = "200", description = "Product updated")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @PatchMapping("/{productId}")
    public ProductResponse updateProduct(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable @Min(1) Long productId,
            @Valid @RequestBody UpdateProductRequest updateProductRequest) {
        return productService.updateProduct(userId, productId, updateProductRequest);
    }

    // [PATCH] /api/product/{productId}/status
    @Operation(summary = "Update product status (soft-delete) by ID")
    @ApiResponse(responseCode = "200", description = "Product status updated")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @PatchMapping("/{productId}/status")
    public ProductResponse updateProductStatus(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable @Min(1) Long productId,
            @Valid @RequestBody UpdateProductStatusRequest updateProductStatusRequest) {
        return productService.updateProductStatus(userId, productId, updateProductStatusRequest);
    }

    // [DELETE] /api/product/{productId}
    @Operation(summary = "Delete product permanently by ID")
    @ApiResponse(responseCode = "204", description = "Product deleted")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable @Min(1) Long productId
    ) {
        productService.deleteProduct(userId, productId);
        return ResponseEntity.noContent().build();
    }
}
