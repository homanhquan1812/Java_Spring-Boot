package org.homanhquan.productservice.repository;

import org.homanhquan.productservice.entity.CartItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CartItemsRepository extends JpaRepository<CartItems, UUID> {
    List<CartItems> findByCartId(UUID cartId);
    void deleteByCartId(UUID cartId);
}