package org.homanhquan.productservice.service;

import org.homanhquan.productservice.dto.common.PageResponse;
import org.homanhquan.productservice.dto.product.request.CreateProductRequest;
import org.homanhquan.productservice.dto.product.request.UpdateProductRequest;
import org.homanhquan.productservice.dto.product.response.ProductResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Why do we use interface for Service?
 * - Allows multiple implementations.
 * - Enables testing with mocks.
 * - Promotes loose coupling (depending on interfaces instead of concrete implementations, making the code easier to change, test, and extend).
 *   private final OrderServiceImpl orderService -> Tight coupling.
 *   private final OrderService orderService -> Loose coupling.
 */
public interface ProductService {

    List<ProductResponse> getAll();
    PageResponse<ProductResponse> getPage(Pageable pageable);
    ProductResponse getById(Long productId);
    ProductResponse create(UUID userId, CreateProductRequest request);
    ProductResponse update(UUID userId, Long productId, UpdateProductRequest request);
    ProductResponse updateStatus(UUID userId, Long productId);
    void delete(UUID userId, Long productId);
}
