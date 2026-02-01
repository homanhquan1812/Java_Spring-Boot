package org.homanhquan.productservice.dto.userInfo.request;

public record UpdateUserInfoRequestByUsers(
        String fullName,
        String username,
        String email,
        String phone,
        String address
) {
}
