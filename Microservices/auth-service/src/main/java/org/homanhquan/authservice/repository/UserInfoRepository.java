package org.homanhquan.authservice.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.homanhquan.authservice.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, UUID> {
    @Query(value = """
    SELECT
        ui.id,
        ui.full_name,
        ui.username,
        ui.email,
        ui.phone,
        ui.address,
        ui.password,
        ui.gender
    FROM user_info ui
    INNER JOIN users u ON u.user_info_id = ui.id
    WHERE u.id = :id
    """, nativeQuery = true)
    Optional<UserInfo> findUsersByUserId(@Param("id") UUID id);
}
