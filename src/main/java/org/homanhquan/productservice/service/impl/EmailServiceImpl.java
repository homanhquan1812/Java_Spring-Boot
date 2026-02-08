package org.homanhquan.productservice.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.productservice.dto.order.response.OrderResponse;
import org.homanhquan.productservice.service.EmailService;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Getter
@Setter
@Service
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "app.email")
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private String fromEmail;
    private String fromName;

    /**
     * Explanation of current @Retryable
     * - maxAttempts: Up to 3 attempts (2 retries).
     * - backoff: First retry waits 2 seconds; second retry waits 2s × 2 = 4 seconds.
     * - retryFor: Retries are triggered only when a MessagingException is thrown.
     */
    @Async
    @Retryable(
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2),
            retryFor = { MessagingException.class }
    )
    @Override
    public void sendWelcomeEmail(String email, String username) {
        log.info("[Thread: {}] Starting to send welcome email to {}",
                Thread.currentThread().getName(), email);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(email);
            helper.setSubject("Welcome to Our Platform!");
            helper.setText(buildWelcomeEmailContent(username), true); // true = HTML

            mailSender.send(mimeMessage);

            log.info("[Thread: {}] Welcome email sent successfully to {}",
                    Thread.currentThread().getName(), email);

        } catch (MessagingException e) {
            log.error("[Thread: {}] Failed to send welcome email to {}: {}",
                    Thread.currentThread().getName(), email, e.getMessage());
            throw new RuntimeException("Email sending failed", e);
        } catch (Exception e) {
            log.error("[Thread: {}] Unexpected error while sending email to {}: {}",
                    Thread.currentThread().getName(), email, e.getMessage(), e);
        }
    }

    @Retryable(
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2),
            retryFor = { MessagingException.class }
    )
    @Override
    public void sendOrderConfirmationEmail(String email, OrderResponse order) {
        log.info("Sending order confirmation email to {}", email);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(email);
            helper.setSubject("Order Confirmation - Order #" + order.id());
            helper.setText(buildOrderEmailContent(order), true);

            mailSender.send(mimeMessage);
            log.info("Order confirmation email sent to {}", email);

        } catch (MessagingException e) {
            log.error("Failed to send order email to {}: {}", email, e.getMessage());
            throw new RuntimeException("Email sending failed", e);
        } catch (Exception e) {
            log.error("Unexpected error sending email to {}: {}", email, e.getMessage(), e);
        }
    }

    private String buildWelcomeEmailContent(String username) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }
                        .content { padding: 20px; background-color: #f9f9f9; }
                        .footer { text-align: center; padding: 10px; font-size: 12px; color: #888; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Welcome to Our Platform!</h1>
                        </div>
                        <div class="content">
                            <h2>Hello %s!</h2>
                            <p>Thank you for registering with us. We're excited to have you on board.</p>
                            <p>You can now start exploring our services and enjoy all the features we offer.</p>
                            <p>If you have any questions, feel free to contact our support team.</p>
                            <p>Best regards,<br>The Team</p>
                        </div>
                        <div class="footer">
                            <p>&copy; 2024 Your Company. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(username);
    }

    private String buildOrderEmailContent(OrderResponse order) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }
                        .content { padding: 20px; background-color: #f9f9f9; }
                        .order-info { background-color: #fff; padding: 15px; border-left: 4px solid #4CAF50; margin: 20px 0; }
                        .footer { text-align: center; padding: 10px; font-size: 12px; color: #888; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Order Confirmation</h1>
                        </div>
                        <div class="content">
                            <h2>Thank you for your order!</h2>
                            <p>Your order has been successfully placed and is being processed.</p>
                            
                            <div class="order-info">
                                <h3>Order Details:</h3>
                                <p><strong>Order ID:</strong> %s</p>
                                <p><strong>Total Amount:</strong> %s VND</p>
                                <p><strong>Payment Method:</strong> %s</p>
                                <p><strong>Status:</strong> %s</p>
                                <p><strong>Order Date:</strong> %s</p>
                            </div>
                            
                            <p>We will notify you once your order is ready for delivery.</p>
                            <p>Best regards,<br><strong>HMQ Cake Shop</strong></p>
                        </div>
                        <div class="footer">
                            <p>&copy; 2024 HMQ Cake Shop. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(
                order.id(),
                order.totalPrice(),
                order.paymentMethod(),
                order.status(),
                order.createdAt()
        );
    }
}