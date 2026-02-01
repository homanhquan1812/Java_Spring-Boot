package org.homanhquan.productservice.repository;

import org.homanhquan.productservice.entity.UserInfo;
import org.homanhquan.productservice.projection.LoginProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoginRepository extends JpaRepository<UserInfo, UUID> {
    @Query(value = """
        SELECT
            COALESCE(u.id, s.id, a.id) AS id,
            ui.username AS username,
            ui.password AS password,
            ui.full_name AS fullName,
            c.id AS cartId,
            CASE
                WHEN a.role IS NOT NULL THEN a.role              -- ưu tiên ADMIN
                WHEN s.role IS NOT NULL THEN s.role              -- tiếp đến role của staff
                WHEN u.role IS NOT NULL THEN u.role              -- cuối cùng role của users
                ELSE NULL
            END AS role
        FROM user_info ui
        LEFT JOIN users u ON u.user_info_id = ui.id
        LEFT JOIN cart c ON c.user_id = u.id
        LEFT JOIN staff s ON s.user_info_id = ui.id
        LEFT JOIN admins a ON a.user_info_id = ui.id
        WHERE username = :username
    """, nativeQuery = true)
    Optional<LoginProjection> findByUsernameAndRole(String username);
}