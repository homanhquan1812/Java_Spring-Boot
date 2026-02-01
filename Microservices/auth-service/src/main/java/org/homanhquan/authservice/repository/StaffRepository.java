package org.homanhquan.authservice.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.homanhquan.authservice.projection.StaffProjection;
import org.homanhquan.authservice.entity.Staff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StaffRepository extends JpaRepository<Staff, UUID> {
    // GET /staff?...=...&...=...
    @Query(value = """
        SELECT
            s.id,
            s.user_info_id,
            s.status,
            s.role,
            s.created_at,
            s.updated_at,
            s.deleted_at,
            ui.full_name,
            ui.username,
            ui.email,
            ui.phone,
            ui.gender,
            ui.address
        FROM staff s
        LEFT JOIN user_info ui on s.user_info_id = ui.id
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM staff s
        LEFT JOIN user_info ui on s.user_info_id = ui.id
        """,
            nativeQuery = true)
    Page<StaffProjection> findAllStaffsWithBrandNameAndParams(Pageable pageable);

    // GET /staff/{id}
    @Query(value = """
        SELECT
            s.id,
            s.user_info_id,
            s.status,
            s.role,
            s.created_at,
            s.updated_at,
            s.deleted_at,
            ui.full_name,
            ui.username,
            ui.email,
            ui.phone,
            ui.gender,
            ui.address
        FROM staff s
        LEFT JOIN user_info ui on s.user_info_id = ui.id
        WHERE s.id = :id
        """, nativeQuery = true)
    Optional<StaffProjection> findAllStaffsWithBrandNameAndParamsById(@Param("id") UUID id);
}
