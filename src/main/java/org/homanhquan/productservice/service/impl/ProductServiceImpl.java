package org.homanhquan.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.productservice.dto.common.PageResponse;
import org.homanhquan.productservice.dto.product.request.CreateProductRequest;
import org.homanhquan.productservice.dto.product.request.UpdateProductRequest;
import org.homanhquan.productservice.dto.product.response.ProductResponse;
import org.homanhquan.productservice.entity.Product;
import org.homanhquan.productservice.enums.Status;
import org.homanhquan.productservice.exception.ResourceNotFoundException;
import org.homanhquan.productservice.mapper.ProductMapper;
import org.homanhquan.productservice.repository.BrandRepository;
import org.homanhquan.productservice.repository.ProductRepository;
import org.homanhquan.productservice.service.ProductService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
    private final BrandRepository brandRepository;

    private Product findProductById(Long productId){
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
    }

    @Override
    @Cacheable(value = "allProducts", key = "'all'")
    public List<ProductResponse> getAll() {
        return productMapper.projectionToDtoList(
                productRepository.findAllProductsWithBrandName()
        );
    }

    @Override
    public PageResponse<ProductResponse> getPage(Pageable pageable) {
        return PageResponse.from(productRepository
                .findProductsPageWithBrandName(pageable)
                .map(productMapper::projectionToDto)
        );
    }

    @Override
    @Cacheable(value = "productById", key = "#productId")
    public ProductResponse getById(Long productId) {
        return productMapper.projectionToDto(
                productRepository.findProductByIdWithBrandName(productId)
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId))
        );
    }

    /**
     * Flow:
     * 1. Map request to Entity.
     * 2. Set status & brand ID for new product.
     * 3. Save the new product and return response.
     *
     * @param userId
     * @param request
     * @return
     */
    @Override
    @CacheEvict(value = "allProducts", allEntries = true)
    @Transactional
    public ProductResponse create(UUID userId, CreateProductRequest request) {
        Product product = productMapper.toEntity(request);

        product.setStatus(Status.ACTIVE);
        product.setBrand(
                brandRepository.findById(request.brandId())
                        .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + request.brandId()))
        );

        Product savedProduct = productRepository.save(product);

        log.info("Product {} created successfully by {}.",
                savedProduct.getId(),
                userId
        );

        return productMapper.toDto(savedProduct);
    }

    /**
     * Flow:
     * 1. Find the product by its ID.
     * 2. Map updated fields from request to entity.
     * 3. Save the updated product and return response.
     *
     * @param userId
     * @param productId
     * @param request
     * @return
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "productById", key = "#productId"),
            @CacheEvict(value = "allProducts", allEntries = true)
    })
    @Transactional
    public ProductResponse update(UUID userId, Long productId, UpdateProductRequest request) {
        Product existingProduct = findProductById(productId);

        productMapper.updateEntityFromDto(request, existingProduct);

        Product updatedProduct = productRepository.save(existingProduct);

        log.info("Product {} updated successfully by {}.",
                updatedProduct.getId(),
                userId
        );

        return productMapper.toDto(updatedProduct);
    }

    /**
     * Flow:
     * 1. Find the product by its ID.
     * 2. Soft-delete: Set status to SUSPENDED and record deletedBy/deletedAt.
     * 3. Save the updated product and return response.
     *
     * @param userId
     * @param productId
     * @return
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "productById", key = "#productId"),
            @CacheEvict(value = "allProducts", allEntries = true)
    })
    @Transactional
    public ProductResponse updateStatus(UUID userId, Long productId) {
        Product existingProduct = findProductById(productId);

        existingProduct.softDelete(userId);

        Product savedProduct = productRepository.save(existingProduct);

        log.info("Product's status {} updated successfully by {}. Status: {}",
                savedProduct.getId(),
                userId,
                savedProduct.getStatus()
        );

        return productMapper.toDto(savedProduct);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "productById", key = "#productId"),
            @CacheEvict(value = "allProducts", allEntries = true)
    })
    @Transactional
    public void delete(UUID userId, Long productId) {
        log.warn("WARNING: User {} is deleting product {}!",
                userId,
                productId
        );

        productRepository.delete(findProductById(productId));

        log.info("Product deleted: id={}", productId);
    }
}