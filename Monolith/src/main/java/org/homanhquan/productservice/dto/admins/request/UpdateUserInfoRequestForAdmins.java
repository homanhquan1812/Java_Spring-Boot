package org.homanhquan.productservice.dto.admins.request;

import org.homanhquan.productservice.enums.Gender;

public record UpdateUserInfoRequestForAdmins(
        String fullName,
        String username,
        String email,
        String phone,
        Gender gender,
        String address
) {
}