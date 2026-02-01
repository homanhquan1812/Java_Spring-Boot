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
    // GET /cart
    @Query(value = """
        SELECT
            ci.name,
            ci.price,
            ci.quantity,
            c.updated_at
        FROM cart_item ci
        LEFT JOIN cart c ON c.id = ci.cart_id
        WHERE c.user_id = :id
        """, nativeQuery = true)
    List<CartItemProjection> findCartWithSpecificItemsById(@Param("id") UUID id);

    Optional<Cart> findByUserId(@Param("id") UUID id);
}
