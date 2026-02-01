package org.homanhquan.productservice.dto.userInfo.response;

public record UserInfoResponseForUsers(
        String fullName,
        String username,
        String email,
        String phone,
        String address
) {
}
