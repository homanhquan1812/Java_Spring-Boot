package org.homanhquan.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.productservice.dto.common.PageResponse;
import org.homanhquan.productservice.dto.product.request.CreateProductRequest;
import org.homanhquan.productservice.dto.product.request.UpdateProductRequest;
import org.homanhquan.productservice.dto.product.request.UpdateProductStatusRequest;
import org.homanhquan.productservice.dto.product.response.ProductResponse;
import org.homanhquan.productservice.entity.Brand;
import org.homanhquan.productservice.entity.Product;
import org.homanhquan.productservice.exception.ResourceNotFoundException;
import org.homanhquan.productservice.mapper.ProductMapper;
import org.homanhquan.productservice.projection.ProductProjection;
import org.homanhquan.productservice.repository.BrandRepository;
import org.homanhquan.productservice.repository.ProductRepository;
import org.homanhquan.productservice.service.ProductService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Hibernate manages entities inside a Persistence Context bound to the transaction scope. During a transaction, Hibernate tracks managed entities.
 * Before commit, it performs dirty checking (detects changed fields). If changes exist, it performs updates/inserts to sync with the database (Flush).
 * ==================================================
 * Annotation/Method explanation:
 * - @Service: A bean for the business logic layer. Technically the same as @Component - A generic Spring bean, but it makes your intent clear.
 * - @Transactional: Manages database transactions automatically. By default: @Transactional(propagation = REQUIRED, readOnly = false)
 *   + Uses readOnly = true for GET, mainly in Service  ⇒ Read-only query (faster, no dirty checking, no accidental writes).
 *   + propagation = REQUIRED: Joins existing transaction (multiple methods in the same callstack) or creates new one if none exists.
 *   Transaction is a set of database operations executed as one unit, which either all succeed or all fail, ensuring data consistency (ACID):
 *   + Atomicity: Fail one, rollback all.
 *   + Consistency: A transaction must not break database or business rules.
 *   + Isolation: Concurrent transactions don’t affect each other.
 *   + Durability: Once a transaction is committed, the data will not be lost.
 * - @RequiredArgsConstructor: Automatically injects dependencies for constructor.
 * - @Slf4j: Provides a logger for logging.
 * - @Cacheable: Caches the method result. If the key exists, the method skips execution. Mainly used for GET.
 * - @CacheEvict: Remove entries from cache. Mainly used for POST, PUT, DELETE.
 * - @CachePut: Overrides the result while keeping the key. Rarely used for PUT because it only updates 1 cache, doesn't clear related caches (lists, pages) → Data inconsistency.
 * - @Caching: Combines multiple cache operations on a single method. Mainly used for PUT, DELETE.
 * - allEntries = false: Clear specific keys (Enabled by default). If true, clear entire keys. Mainly used in @CacheEvict.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final BrandRepository brandRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "allProducts", key = "'all'")
    public List<ProductResponse> getAllProducts() {
        return productMapper.projectionToDtoList(productRepository.findAllProductsWithBrandName());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getProductsPage(Pageable pageable) {
        Page<ProductResponse> page = productRepository
                .findProductsPageWithBrandName(pageable)
                .map(productMapper::projectionToDto);
        return PageResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "productById", key = "#productId")
    public ProductResponse getProductById(Long productId) {
        ProductProjection productProjection = productRepository.findProductByIdWithBrandName(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        return productMapper.projectionToDto(productProjection);
    }

    @Override
    @CacheEvict(value = "allProducts", allEntries = true)
    public ProductResponse createProduct(UUID userId, CreateProductRequest createProductRequest) {
        Product product = productMapper.toEntity(createProductRequest);

        product.setBrand(findBrand(createProductRequest.brandId()));
        product.setCreatedBy(userId);
        product.setUpdatedBy(userId);

        log.info("Product {} created successfully by {}.", product.getId(), userId);

        return productMapper.toDto(productRepository.save(product));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "productById", key = "#productId"),
            @CacheEvict(value = "allProducts", allEntries = true)
    })
    public ProductResponse updateProduct(UUID userId, Long productId, UpdateProductRequest updateProductRequest) {
        Product existingProduct = findProduct(productId);
        existingProduct.setUpdatedBy(userId);
        productMapper.updateEntityFromDto(updateProductRequest, existingProduct);
        return productMapper.toDto(productRepository.save(existingProduct));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "productById", key = "#productId"),
            @CacheEvict(value = "allProducts", allEntries = true)
    })
    public ProductResponse updateProductStatus(UUID userId, Long productId, UpdateProductStatusRequest updateProductStatusRequest) {
        Product existingProduct = findProduct(productId);
        productMapper.updateEntityFromDtoForStatus(updateProductStatusRequest, existingProduct);

        existingProduct.setDeletedAt(LocalDateTime.now());
        existingProduct.setDeletedBy(userId);

        return productMapper.toDto(productRepository.save(existingProduct));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "productById", key = "#productId"),
            @CacheEvict(value = "allProducts", allEntries = true)
    })
    public void deleteProduct(UUID userId, Long productId) {
        log.warn("Deleting product: id={}, user={}", productId, userId);

        Product existingProduct = findProduct(productId);
        productRepository.delete(existingProduct);

        log.info("Product deleted: id={}", productId);
    }

    /**
     * Helper methods
     */
    private Brand findBrand(Long brandId) {
        return brandRepository.findById(brandId)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + brandId));
    }

    private Product findProduct(Long productId){
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
    }
}