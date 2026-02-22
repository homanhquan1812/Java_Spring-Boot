package org.homanhquan.productservice.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.homanhquan.productservice.entity.Cart;
import org.homanhquan.productservice.projection.CartItemProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    @Query(value = """
        SELECT
            ci.name,
            ci.price,
            ci.quantity
        FROM CartItem ci
        LEFT JOIN Cart c ON c.id = ci.cartId
        WHERE c.userId = :id
        """)
    List<CartItemProjection> findCartWithSpecificItemsById(@Param("id") UUID id);

    Optional<Cart> findByUserId(@Param("id") UUID id);
}
