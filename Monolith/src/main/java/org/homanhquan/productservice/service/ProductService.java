package org.homanhquan.productservice.service;

import org.homanhquan.productservice.dto.common.PageResponse;
import org.homanhquan.productservice.dto.product.request.CreateProductRequest;
import org.homanhquan.productservice.dto.product.request.UpdateProductRequest;
import org.homanhquan.productservice.dto.product.request.UpdateProductStatusRequest;
import org.homanhquan.productservice.dto.product.response.ProductResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    PageResponse<ProductResponse> getProductsPage(Pageable pageable);
    ProductResponse getProductById(Long productId);
    ProductResponse createProduct(UUID userId, CreateProductRequest createProductRequest);
    ProductResponse updateProduct(UUID userId, Long productId, UpdateProductRequest updateProductRequest);
    ProductResponse updateProductStatus(UUID userId, Long productId, UpdateProductStatusRequest updateProductStatusRequest);
    void deleteProduct(UUID userId, Long productId);
}
