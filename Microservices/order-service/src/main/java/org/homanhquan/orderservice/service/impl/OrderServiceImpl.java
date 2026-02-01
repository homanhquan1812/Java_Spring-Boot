package org.homanhquan.orderservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.homanhquan.orderservice.dto.orderItem.response.OrderItemResponse;
import org.homanhquan.orderservice.dto.order.request.CreateOrderRequest;
import org.homanhquan.orderservice.dto.order.request.UpdateOrderStatusRequest;
import org.homanhquan.orderservice.dto.order.response.OrderResponse;
import org.homanhquan.orderservice.entity.Cart;
import org.homanhquan.orderservice.entity.CartItem;
import org.homanhquan.orderservice.entity.Order;
import org.homanhquan.orderservice.entity.OrderItem;
import org.homanhquan.orderservice.enums.Status;
import org.homanhquan.orderservice.exception.ResourceNotFoundException;
import org.homanhquan.orderservice.mapper.CartItemMapper;
import org.homanhquan.orderservice.mapper.CartMapper;
import org.homanhquan.orderservice.mapper.OrderItemMapper;
import org.homanhquan.orderservice.mapper.OrderMapper;
import org.homanhquan.orderservice.repository.CartItemRepository;
import org.homanhquan.orderservice.repository.CartRepository;
import org.homanhquan.orderservice.repository.OrderItemRepository;
import org.homanhquan.orderservice.repository.OrderRepository;
import org.homanhquan.orderservice.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper ordersMapper;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;
    private final CartItemRepository cartItemRepository;

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders(UUID userId) {
        return ordersMapper.toDtoList(orderRepository.findByUserId(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemResponse> getSpecificOrder(UUID userId, UUID orderId) {
        return orderItemMapper.toDtoList(orderItemRepository.findByOrderId(orderId));
    }

    @Override
    public OrderResponse createOrder(UUID userId, CreateOrderRequest createOrderRequest) {
        // 1. Lấy giỏ hàng của user
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user"));

        // 2. Lấy tất cả items trong giỏ hàng
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());

        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cannot create order from empty cart");
        }

        // 3. Tính tổng giá trị đơn hàng
        BigDecimal totalPrice = cartItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. Tạo order mới
        Order order = Order.builder()
                .userId(userId)
                .totalPrice(totalPrice)
                .status(Status.PENDING)
                .paymentMethod(createOrderRequest.paymentMethod())
                .build();

        Order savedOrder = orderRepository.save(order);

        // 5. Tạo order items từ cart items
        List<OrderItem> orderItem = cartItems.stream()
                .map(cartItem -> OrderItem.builder()
                        .orderId(savedOrder.getId())
                        .productId(cartItem.getProductId())
                        .name(cartItem.getName())
                        .price(cartItem.getPrice())
                        .quantity(cartItem.getQuantity())
                        .build())
                .toList();

        orderItemRepository.saveAll(orderItem);

        // 6. Xóa tất cả items trong giỏ hàng (làm trống giỏ)
        cartItemRepository.deleteByCartId(cart.getId());

        // 7. Trả về response
        return OrderResponse.builder()
                .orderId(savedOrder.getId())
                .userId(savedOrder.getUserId())
                .totalPrice(savedOrder.getTotalPrice())
                .status(savedOrder.getStatus())
                .paymentMethod(savedOrder.getPaymentMethod())
                .itemCount(orderItem.size())
                .createdAt(savedOrder.getCreatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersFromAllUsers(UUID staffId) {
        return null;
    }

    @Override
    public OrderResponse updateOrderStatus(UUID staffId, UUID orderId, UpdateOrderStatusRequest updateOrderStatusRequest) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + orderId));
        ordersMapper.updateEntityFromDto(updateOrderStatusRequest, order);
        return ordersMapper.toDto(orderRepository.save(order));
    }
}
