package org.homanhquan.productservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.homanhquan.productservice.entity.common.DateAuditable;
import org.homanhquan.productservice.enums.PaymentMethod;
import org.homanhquan.productservice.enums.Status;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order extends DateAuditable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UNIQUEIDENTIFIER", updatable = false, nullable = false)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "total_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 20)
    private PaymentMethod paymentMethod;

    // Static Factory Method
    public static Order of(UUID userId, BigDecimal totalPrice, Status status, PaymentMethod paymentMethod) {
        Order order = new Order();

        order.userId = userId;
        order.totalPrice = totalPrice;
        order.status = status;
        order.paymentMethod = paymentMethod;

        return order;
    }

    public void setDefaultStatus() {
        this.status = Status.PENDING;
    }
}
