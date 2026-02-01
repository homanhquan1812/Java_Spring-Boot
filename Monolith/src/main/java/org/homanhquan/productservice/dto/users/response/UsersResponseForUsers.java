package org.homanhquan.productservice.dto.users.response;

import org.homanhquan.productservice.enums.Gender;

import java.time.LocalDateTime;

public record UsersResponseForUsers(
        String fullName,
        String username,
        String brandName,
        String email,
        String phone,
        Gender gender,
        String address,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
