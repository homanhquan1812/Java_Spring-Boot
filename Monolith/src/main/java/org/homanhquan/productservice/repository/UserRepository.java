package org.homanhquan.productservice.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.homanhquan.productservice.entity.User;
import org.homanhquan.productservice.projection.UserProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    // GET /user?...=...&...=...
    @Query(value = """
        SELECT
            u.id,
            u.user_info_id,
            u.status,
            u.role,
            u.created_at,
            u.updated_at,
            u.deleted_at,
            ui.full_name,
            ui.username,
            ui.email,
            ui.phone,
            ui.gender,
            ui.address
        FROM user u
        LEFT JOIN user_info ui ON u.user_info_id = ui.id
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM user u
        LEFT JOIN user_info ui ON u.user_info_id = ui.id
        """,
            nativeQuery = true)
    Page<UserProjection> findAllUsersWithBrandNameAndParams(Pageable pageable);

    // GET /user/my-info
    @Query(value = """
        SELECT
            u.created_at,
            u.updated_at,
            ui.full_name,
            ui.username,
            b.name AS brandName,
            ui.email,
            ui.phone,
            ui.gender,
            ui.address
        FROM user u
        LEFT JOIN user_info ui ON u.user_info_id = ui.id
        LEFT JOIN brand b ON b.id = u.brand_id
        WHERE u.id = :id
        """, nativeQuery = true)
    Optional<UserProjection> findUserInfoWithBrandNameById(@Param("id") UUID id);
}
