package org.homanhquan.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.productservice.dto.common.PageResponse;
import org.homanhquan.productservice.dto.orderItem.response.OrderItemResponse;
import org.homanhquan.productservice.dto.order.request.CreateOrderRequest;
import org.homanhquan.productservice.dto.order.request.UpdateOrderStatusRequest;
import org.homanhquan.productservice.dto.order.response.OrderResponse;
import org.homanhquan.productservice.entity.Cart;
import org.homanhquan.productservice.entity.CartItem;
import org.homanhquan.productservice.entity.Order;
import org.homanhquan.productservice.exception.ResourceNotFoundException;
import org.homanhquan.productservice.mapper.OrderItemsMapper;
import org.homanhquan.productservice.mapper.OrderMapper;
import org.homanhquan.productservice.repository.OrderItemRepository;
import org.homanhquan.productservice.repository.OrderRepository;
import org.homanhquan.productservice.service.OrderService;
import org.homanhquan.productservice.service.helper.OrderCreationHelper;
import org.springframework.data.domain.Page;
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
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemsMapper orderItemsMapper;
    private final OrderCreationHelper orderCreationHelper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getAllOrders(Pageable pageable, UUID userId) {
        Page<OrderResponse> page = orderRepository
                .findByUserId(pageable, userId)
                .map(orderMapper::toDto);
        return PageResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemResponse> getSpecificOrder(UUID userId, UUID orderId) {
        return orderItemsMapper.toDtoList(orderItemRepository.findByOrderId(orderId));
    }

    /**
     * Creates a new order from the user's cart items.
     *
     * This method orchestrates the order creation process by:
     * 1. Validating and retrieving the user's cart
     * 2. Validating cart items (ensuring cart is not empty)
     * 3. Calculating the total price from all cart items
     * 4. Creating and saving the order with PENDING status
     * 5. Publishing an order created event to RabbitMQ for asynchronous processing
     *    (OrderItems creation and cart cleanup will be handled by the event consumer)
     *
     * @param userId the UUID of the user creating the order
     * @param createOrderRequest the order creation request containing payment method
     * @return OrderResponse containing the created order details
     * @throws ResourceNotFoundException if the cart is not found for the user
     * @throws IllegalStateException if the cart is empty
     */
    @Override
    public OrderResponse createOrder(UUID userId, CreateOrderRequest createOrderRequest) {
        Cart cart = orderCreationHelper.validateAndGetCart(userId);
        List<CartItem> cartItems = orderCreationHelper.validateCartItems(cart.getId());
        BigDecimal totalPrice = orderCreationHelper.calculateTotalPrice(cartItems);
        Order savedOrder = orderCreationHelper.createAndSaveOrder(userId, totalPrice, createOrderRequest);
        orderCreationHelper.publishOrderCreatedEvent(savedOrder, userId, cartItems);

        return orderMapper.toDto(savedOrder);
        /*
        // 1. Lấy giỏ hàng của user
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user"));

        // 2. Lấy tất cả items trong giỏ hàng
        List<CartItem> cartItems = cartItemsRepository.findByCartId(cart.getId());

        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cannot create order from empty cart");
        }

        // 3. Tính tổng giá trị đơn hàng
        BigDecimal totalPrice = cartItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. Tạo order mới
        Order order = new Order();
        order.setUserId(userId);
        order.setTotalPrice(totalPrice);
        order.setStatus(Status.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setPaymentMethod(createOrderRequest.paymentMethod());

        Order savedOrder = orderRepository.save(order);

        // 5. Tạo order items từ cart items
        List<OrderItem> orderItems = cartItems.stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrderId(savedOrder.getId());
                    orderItem.setProductId(cartItem.getProductId());
                    orderItem.setName(cartItem.getName());
                    orderItem.setPrice(cartItem.getPrice());
                    orderItem.setQuantity(cartItem.getQuantity());
                    return orderItem;
                })
                .toList();

        orderItemRepository.saveAll(orderItems);

        // 6. Xóa tất cả items trong giỏ hàng (làm trống giỏ)
        cartItemsRepository.deleteByCartId(cart.getId());

        // 7. Trả về response
        return OrderResponse.builder()
                .id(savedOrder.getId())
                .userId(savedOrder.getUserId())
                .totalPrice(savedOrder.getTotalPrice())
                .status(savedOrder.getStatus())
                .paymentMethod(savedOrder.getPaymentMethod())
                .createdAt(savedOrder.getCreatedAt())
                .build();

         */
    }

    @Override
    public OrderResponse updateOrderStatus(UUID userId, UUID orderId, UpdateOrderStatusRequest updateOrderStatusRequest) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + orderId));
        orderMapper.updateEntityFromDto(updateOrderStatusRequest, order);
        return orderMapper.toDto(orderRepository.save(order));
    }
}
