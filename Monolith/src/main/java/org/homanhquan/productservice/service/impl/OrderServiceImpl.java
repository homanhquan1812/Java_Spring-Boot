package org.homanhquan.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.productservice.dto.order.event.CartItemSnapshot;
import org.homanhquan.productservice.dto.order.event.OrderCreatedEvent;
import org.homanhquan.productservice.dto.orderItem.response.OrderItemsResponse;
import org.homanhquan.productservice.dto.order.request.CreateOrderRequest;
import org.homanhquan.productservice.dto.order.request.UpdateOrderStatusRequest;
import org.homanhquan.productservice.dto.order.response.OrderResponse;
import org.homanhquan.productservice.entity.Cart;
import org.homanhquan.productservice.entity.CartItem;
import org.homanhquan.productservice.entity.Order;
import org.homanhquan.productservice.enums.Status;
import org.homanhquan.productservice.exception.ResourceNotFoundException;
import org.homanhquan.productservice.mapper.CartItemsMapper;
import org.homanhquan.productservice.mapper.CartMapper;
import org.homanhquan.productservice.mapper.OrderItemsMapper;
import org.homanhquan.productservice.mapper.OrderMapper;
import org.homanhquan.productservice.repository.CartItemsRepository;
import org.homanhquan.productservice.repository.CartRepository;
import org.homanhquan.productservice.repository.OrderItemRepository;
import org.homanhquan.productservice.repository.OrderRepository;
import org.homanhquan.productservice.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemsMapper orderItemsMapper;
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final CartItemsMapper cartItemsMapper;
    private final CartItemsRepository cartItemsRepository;
    private final OrderEventPublisherImpl orderEventPublisher;

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders(UUID userId) {
        return orderMapper.toDtoList(orderRepository.findByUserId(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemsResponse> getSpecificOrder(UUID userId, UUID orderId) {
        return orderItemsMapper.toDtoList(orderItemRepository.findByOrderId(orderId));
    }

    @Override
    public OrderResponse createOrder(UUID userId, CreateOrderRequest createOrderRequest) {
        // 1. Lấy giỏ hàng
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user"));

        // 2. Lấy items
        List<CartItem> cartItems = cartItemsRepository.findByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cannot create order from empty cart");
        }

        // 3. Tính tổng giá
        BigDecimal totalPrice = cartItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. Save order với status PENDING
        Order order = new Order();
        order.setUserId(userId);
        order.setTotalPrice(totalPrice);
        order.setStatus(Status.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setPaymentMethod(createOrderRequest.paymentMethod());

        Order savedOrder = orderRepository.save(order);

        // 5. Publish event → RabbitMQ sẽ lo phần còn lại
        List<CartItemSnapshot> snapshots = cartItems.stream()
                .map(item -> new CartItemSnapshot(
                        item.getProductId(),
                        item.getName(),
                        item.getPrice(),
                        item.getQuantity()
                ))
                .toList();

        orderEventPublisher.publishOrderCreated(
                new OrderCreatedEvent(savedOrder.getId(), userId, snapshots)
        );

        // 6. Return ngay — không cần chờ OrderItems hay xóa cart
        return OrderResponse.builder()
                .id(savedOrder.getId())
                .userId(savedOrder.getUserId())
                .totalPrice(savedOrder.getTotalPrice())
                .status(savedOrder.getStatus())
                .paymentMethod(savedOrder.getPaymentMethod())
                .createdAt(savedOrder.getCreatedAt())
                .build();
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
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersFromAllUsers(UUID userId) {
        return orderMapper.toDtoList(orderRepository.findAll());
    }

    @Override
    public OrderResponse updateOrderStatus(UUID userId, UUID orderId, UpdateOrderStatusRequest updateOrderStatusRequest) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + orderId));
        orderMapper.updateEntityFromDto(updateOrderStatusRequest, order);
        return orderMapper.toDto(orderRepository.save(order));
    }
}
