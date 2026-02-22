package org.homanhquan.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.productservice.dto.common.PageResponse;
import org.homanhquan.productservice.dto.product.request.CreateProductRequest;
import org.homanhquan.productservice.dto.product.request.UpdateProductRequest;
import org.homanhquan.productservice.dto.product.request.UpdateProductStatusRequest;
import org.homanhquan.productservice.dto.product.response.ProductResponse;
import org.homanhquan.productservice.entity.Product;
import org.homanhquan.productservice.enums.Status;
import org.homanhquan.productservice.mapper.ProductMapper;
import org.homanhquan.productservice.repository.ProductRepository;
import org.homanhquan.productservice.service.ProductService;
import org.homanhquan.productservice.service.helper.product.ProductServiceImplHelper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductServiceImplHelper orderHelper;

    /**
     * Retrieves all products with their brand names.
     * @return List of ProductResponse.
     */
    @Override
    @Cacheable(value = "allProducts", key = "'all'")
    public List<ProductResponse> getAllProducts() {
        return productMapper.projectionToDtoList(
                productRepository.findAllProductsWithBrandName()
        );
    }

    /**
     * Retrieves a paginated list of products with their brand names.
     * @param pageable pagination and sorting parameters.
     * @return PageResponse of ProductResponse.
     */
    @Override
    public PageResponse<ProductResponse> getProductsPage(Pageable pageable) {
        Page<ProductResponse> page = productRepository
                .findProductsPageWithBrandName(pageable)
                .map(productMapper::projectionToDto);

        return PageResponse.from(page);
    }

    /**
     * Retrieves a product with its brand name by ID.
     */
    @Override
    @Cacheable(value = "productById", key = "#productId")
    public ProductResponse getProductById(Long productId) {
        return productMapper.projectionToDto(
                orderHelper.findProductProjectionById(productId)
        );
    }

    /**
     * Creates a new product.
     */
    @Override
    @CacheEvict(value = "allProducts", allEntries = true)
    @Transactional
    public ProductResponse createProduct(UUID userId, CreateProductRequest request) {
        Product product = productMapper.toEntity(request);

        product.setStatus(Status.ACTIVE);
        product.setBrand(orderHelper.findBrandById(request.brandId()));

        Product savedProduct = productRepository.save(product);

        log.info("Product {} created successfully by {}.",
                savedProduct.getId(),
                userId
        );

        return productMapper.toDto(savedProduct);
    }

    /**
     * Updates an existing product.
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "productById", key = "#productId"),
            @CacheEvict(value = "allProducts", allEntries = true)
    })
    @Transactional
    public ProductResponse updateProduct(UUID userId, Long productId, UpdateProductRequest request) {
        Product existingProduct = orderHelper.findProductById(productId);

        productMapper.updateEntityFromDto(request, existingProduct);

        Product updatedProduct = productRepository.save(existingProduct);

        log.info("Product {} updated successfully by {}.",
                updatedProduct.getId(),
                userId
        );

        return productMapper.toDto(updatedProduct);
    }

    /**
     * Soft-deletes an existing product by updating its status.
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "productById", key = "#productId"),
            @CacheEvict(value = "allProducts", allEntries = true)
    })
    @Transactional
    public ProductResponse updateProductStatus(UUID userId, Long productId, UpdateProductStatusRequest request) {
        Product existingProduct = orderHelper.findProductById(productId);

        productMapper.updateStatusFromDto(request, existingProduct);

        existingProduct.softDelete(userId);

        Product savedProduct = productRepository.save(existingProduct);

        log.info("Product's status {} updated successfully by {}. Status: {}",
                savedProduct.getId(),
                userId,
                savedProduct.getStatus()
        );

        return productMapper.toDto(savedProduct);
    }

    /**
     * Hard-deletes an existing product.
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "productById", key = "#productId"),
            @CacheEvict(value = "allProducts", allEntries = true)
    })
    @Transactional
    public void deleteProduct(UUID userId, Long productId) {
        log.warn("WARNING: User {} is deleting product {}!",
                userId,
                productId
        );

        productRepository.delete(orderHelper.findProductById(productId));

        log.info("Product deleted: id={}", productId);
    }
}