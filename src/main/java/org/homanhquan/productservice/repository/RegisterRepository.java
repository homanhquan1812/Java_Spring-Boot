package org.homanhquan.productservice.repository;

import org.homanhquan.productservice.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RegisterRepository extends JpaRepository<UserInfo, UUID> {
    Optional<UserInfo> findByEmail(String email);
}
