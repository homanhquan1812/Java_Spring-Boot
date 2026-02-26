package org.homanhquan.productservice.service.helper.auth.login;

import org.homanhquan.productservice.dto.login.response.LoginResponse;
import org.homanhquan.productservice.projection.UserInfoProjection;
import org.homanhquan.productservice.security.userDetails.CustomUserDetails;
import org.springframework.stereotype.Component;

@Component
public class LoginMapper {

    public CustomUserDetails toUserDetails(UserInfoProjection user) {
        return CustomUserDetails.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    public LoginResponse toLoginResponse(UserInfoProjection user, String token) {
        return LoginResponse.builder()
                .id(user.getId())
                .token(token)
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }
}
