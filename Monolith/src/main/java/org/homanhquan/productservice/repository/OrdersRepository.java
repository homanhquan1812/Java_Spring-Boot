package org.homanhquan.productservice.repository;

import org.homanhquan.productservice.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrdersRepository extends JpaRepository<Orders, UUID> {

    List<Orders> findByUserId(UUID userId);
}
