package org.homanhquan.productservice.service.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.productservice.dto.order.event.CartItemSnapshot;
import org.homanhquan.productservice.dto.order.response.OrderResponse;
import org.homanhquan.productservice.entity.*;
import org.homanhquan.productservice.enums.Status;
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
    private final CartItemsRepository cartItemsRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final UserInfoRepository userInfoRepository;
    private final EmailService emailService;

    /**
     * Saves order items from cart item snapshots.
     *
     * @param orderId the order ID
     * @param snapshots the list of cart item snapshots
     */
    public void saveOrderItems(UUID orderId, List<CartItemSnapshot> snapshots) {
        List<OrderItem> orderItems = snapshots.stream()
                .map(item -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrderId(orderId);
                    orderItem.setProductId(item.productId());
                    orderItem.setName(item.name());
                    orderItem.setPrice(item.price());
                    orderItem.setQuantity(item.quantity());
                    return orderItem;
                })
                .toList();

        orderItemRepository.saveAll(orderItems);
        log.debug("Saved {} order items for orderId: {}", orderItems.size(), orderId);
    }

    /**
     * Clears all items from the user's cart.
     *
     * @param userId the user ID
     * @throws ResourceNotFoundException if cart is not found
     */
    public void clearUserCart(UUID userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        cartItemsRepository.deleteByCartId(cart.getId());
        log.debug("Cleared cart items for userId: {}", userId);
    }

    /**
     * Updates the order status to PENDING.
     *
     * @param orderId the order ID
     * @return the updated order
     * @throws ResourceNotFoundException if order is not found
     */
    public Order updateOrderStatus(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setStatus(Status.PENDING);
        Order savedOrder = orderRepository.save(order);

        log.debug("Updated order status to PENDING for orderId: {}", orderId);
        return savedOrder;
    }

    /**
     * Sends order confirmation email to the user.
     *
     * @param userId the user ID
     * @param order the order entity
     */
    public void sendOrderConfirmationEmail(UUID userId, Order order) {
        try {
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

        } catch (Exception e) {
            // Log error but don't throw - email failure shouldn't fail the entire order process
            log.error("Failed to send order confirmation email for orderId {}: {}",
                    order.getId(), e.getMessage());
        }
    }
}