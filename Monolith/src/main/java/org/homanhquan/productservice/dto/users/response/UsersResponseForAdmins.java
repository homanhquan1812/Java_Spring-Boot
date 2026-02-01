package org.homanhquan.productservice.dto.users.response;

import org.homanhquan.productservice.enums.Gender;
import org.homanhquan.productservice.enums.Role;
import org.homanhquan.productservice.enums.Status;

import java.time.LocalDateTime;
import java.util.UUID;

public record UsersResponseForAdmins(
        UUID id,
        UUID userInfoId,
        Status status,
        Role role,
        String fullName,
        String username,
        String email,
        String phone,
        Gender gender,
        String address,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
}
