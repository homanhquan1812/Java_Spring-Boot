package org.homanhquan.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.productservice.dto.common.PageResponse;
import org.homanhquan.productservice.dto.orderItem.response.OrderItemResponse;
import org.homanhquan.productservice.dto.order.request.CheckoutRequest;
import org.homanhquan.productservice.dto.order.request.UpdateOrderStatusRequest;
import org.homanhquan.productservice.dto.order.response.OrderResponse;
import org.homanhquan.productservice.entity.Cart;
import org.homanhquan.productservice.entity.CartItem;
import org.homanhquan.productservice.entity.Order;
import org.homanhquan.productservice.enums.Role;
import org.homanhquan.productservice.exception.ResourceNotFoundException;
import org.homanhquan.productservice.mapper.OrderItemsMapper;
import org.homanhquan.productservice.mapper.OrderMapper;
import org.homanhquan.productservice.repository.*;
import org.homanhquan.productservice.service.OrderService;
import org.homanhquan.productservice.service.helper.order.OrderCreationHelper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Annotation/Method definition:
 * - @Service: A bean for the business logic layer. Technically the same as @Component - A generic Spring bean, but it makes your intent clear.
 * - @Transactional: Manages database transactions automatically. By default: @Transactional(propagation = REQUIRED, readOnly = false)
 *   + Uses readOnly = true for GET, mainly in Service  ⇒ Read-only query (faster, no dirty checking, no accidental writes).
 *   + propagation = REQUIRED: Joins existing transaction (multiple methods in the same callstack) or creates new one if none exists.
 *
 *   Hibernate tracks entities in the persistence context during a transaction.
 *   Before commit, it performs dirty checking (detects changed fields). If changes exist, it performs updates/inserts to sync with the database (Flush).
 *
 * - @Cacheable: Caches the method result. If the key exists, the method skips execution. Mainly used for GET.
 * - @CacheEvict: Remove entries from cache. Mainly used for POST, PUT, DELETE.
 * - @CachePut: Overrides the result while keeping the key. Rarely used for PUT because it only updates 1 cache, doesn't clear related caches (lists, pages) → Data inconsistency.
 * - @Caching: Combines multiple cache operations on a single method. Mainly used for PUT, DELETE.
 * - allEntries = false: Clear specific keys (Enabled by default). If true, clear entire keys. Mainly used in @CacheEvict.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemsMapper orderItemsMapper;

    @Override
    public PageResponse<OrderResponse> getPage(Pageable pageable, UUID userId) {
        return PageResponse.from(orderRepository
                .findByUserId(pageable, userId)
                .map(orderMapper::toDto)
        );
    }

    @Override
    public List<OrderItemResponse> getById(UUID userId, Role role, UUID orderId) {
        return orderItemsMapper.toDtoList(
                orderItemRepository.findByOrderId(orderId)
        );
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(UUID userId, UUID orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + orderId));

        order.changeStatus(request.status());

        return orderMapper.toDto(orderRepository.save(order));
    }
}
