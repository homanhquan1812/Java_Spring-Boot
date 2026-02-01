package org.homanhquan.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.productservice.dto.orderItems.response.OrderItemsResponse;
import org.homanhquan.productservice.dto.orders.request.CreateOrdersRequest;
import org.homanhquan.productservice.dto.orders.request.UpdateOrderStatusRequest;
import org.homanhquan.productservice.dto.orders.response.OrdersResponse;
import org.homanhquan.productservice.entity.*;
import org.homanhquan.productservice.enums.Status;
import org.homanhquan.productservice.exception.ResourceNotFoundException;
import org.homanhquan.productservice.mapper.CartItemsMapper;
import org.homanhquan.productservice.mapper.CartMapper;
import org.homanhquan.productservice.mapper.OrderItemsMapper;
import org.homanhquan.productservice.mapper.OrdersMapper;
import org.homanhquan.productservice.repository.CartItemsRepository;
import org.homanhquan.productservice.repository.CartRepository;
import org.homanhquan.productservice.repository.OrderItemsRepository;
import org.homanhquan.productservice.repository.OrdersRepository;
import org.homanhquan.productservice.service.OrdersService;
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
public class OrdersServiceImpl implements OrdersService {

    private final OrdersRepository ordersRepository;
    private final OrdersMapper ordersMapper;
    private final OrderItemsRepository orderItemsRepository;
    private final OrderItemsMapper orderItemsMapper;
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final CartItemsMapper cartItemsMapper;
    private final CartItemsRepository cartItemsRepository;

    @Override
    @Transactional(readOnly = true)
    public List<OrdersResponse> getAllOrders(UUID userId) {
        return ordersMapper.toDtoList(ordersRepository.findByUserId(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemsResponse> getSpecificOrder(UUID userId, UUID orderId) {
        return orderItemsMapper.toDtoList(orderItemsRepository.findByOrderId(orderId));
    }

    @Override
    public OrdersResponse createOrder(UUID userId, CreateOrdersRequest createOrdersRequest) {
        // 1. Lấy giỏ hàng của user
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user"));

        // 2. Lấy tất cả items trong giỏ hàng
        List<CartItems> cartItems = cartItemsRepository.findByCartId(cart.getId());

        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cannot create order from empty cart");
        }

        // 3. Tính tổng giá trị đơn hàng
        BigDecimal totalPrice = cartItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. Tạo order mới
        Orders order = new Orders();
        order.setUserId(userId);
        order.setTotalPrice(totalPrice);
        order.setStatus(Status.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setPaymentMethod(createOrdersRequest.paymentMethod());

        Orders savedOrder = ordersRepository.save(order);

        // 5. Tạo order items từ cart items
        List<OrderItems> orderItems = cartItems.stream()
                .map(cartItem -> {
                    OrderItems orderItem = new OrderItems();
                    orderItem.setOrderId(savedOrder.getId());
                    orderItem.setProductId(cartItem.getProductId());
                    orderItem.setName(cartItem.getName());
                    orderItem.setPrice(cartItem.getPrice());
                    orderItem.setQuantity(cartItem.getQuantity());
                    return orderItem;
                })
                .toList();

        orderItemsRepository.saveAll(orderItems);

        // 6. Xóa tất cả items trong giỏ hàng (làm trống giỏ)
        cartItemsRepository.deleteByCartId(cart.getId());

        // 7. Trả về response
        return OrdersResponse.builder()
                .id(savedOrder.getId())
                .userId(savedOrder.getUserId())
                .totalPrice(savedOrder.getTotalPrice())
                .status(savedOrder.getStatus())
                .paymentMethod(savedOrder.getPaymentMethod())
                .createdAt(savedOrder.getCreatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdersResponse> getOrdersFromAllUsers(UUID userId) {
        return ordersMapper.toDtoList(ordersRepository.findAll());
    }

    @Override
    public OrdersResponse updateOrderStatus(UUID userId, UUID orderId, UpdateOrderStatusRequest updateOrderStatusRequest) {
        Orders orders = ordersRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + orderId));
        ordersMapper.updateEntityFromDto(updateOrderStatusRequest, orders);
        return ordersMapper.toDto(ordersRepository.save(orders));
    }
}
