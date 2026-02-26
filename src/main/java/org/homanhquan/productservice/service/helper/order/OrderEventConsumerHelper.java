package org.homanhquan.productservice.service.helper.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.productservice.event.order.CartItemSnapshot;
import org.homanhquan.productservice.dto.order.response.OrderResponse;
import org.homanhquan.productservice.entity.*;
import org.homanhquan.productservice.exception.ResourceNotFoundException;
import org.homanhquan.productservice.repository.*;
import org.homanhquan.productservice.service.EmailService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumerHelper {

    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final UserInfoRepository userInfoRepository;
    private final EmailService emailService;

    public void saveOrderItems(UUID orderId, List<CartItemSnapshot> snapshots) {
        List<OrderItem> orderItems = snapshots.stream()
                .map(item -> OrderItem.of(
                        orderId,
                        item.productId(),
                        item.name(),
                        item.price(),
                        item.quantity()
                ))
                .toList();

        orderItemRepository.saveAll(orderItems);
        log.debug("Saved {} order items for orderId: {}", orderItems.size(), orderId);
    }

    public void clearUserCart(UUID userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        cartItemRepository.deleteByCartId(cart.getId());
        log.debug("Cleared cart items for userId: {}", userId);
    }

    public Order updateOrderStatus(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setDefaultStatus();
        Order savedOrder = orderRepository.save(order);

        log.debug("Updated order status to PENDING for orderId: {}", orderId);
        return savedOrder;
    }

    public void sendOrderConfirmationEmail(UUID userId, Order order) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserInfo userInfo = userInfoRepository.findById(user.getUserInfoId())
                .orElseThrow(() -> new ResourceNotFoundException("UserInfo not found"));

        OrderResponse orderResponse = OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .createdAt(order.getCreatedAt())
                .build();

        emailService.sendOrderConfirmationEmail(userInfo.getEmail(), orderResponse);
    }
}