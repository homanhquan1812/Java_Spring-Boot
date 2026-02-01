package org.homanhquan.productservice.repository;

import org.homanhquan.productservice.entity.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderItemsRepository extends JpaRepository<OrderItems, UUID> {
    // GET /users/list/orders/{orderId}
    List<OrderItems> findByOrderId(UUID orderId);
}