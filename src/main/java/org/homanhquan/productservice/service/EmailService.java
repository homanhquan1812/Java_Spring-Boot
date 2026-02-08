package org.homanhquan.productservice.service;

import org.homanhquan.productservice.dto.order.response.OrderResponse;

public interface EmailService {

    void sendWelcomeEmail(String email, String username);
    void sendOrderConfirmationEmail(String email, OrderResponse order);
}
