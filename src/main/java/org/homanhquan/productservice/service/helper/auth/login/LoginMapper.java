package org.homanhquan.productservice.service.helper.auth.login;

import org.homanhquan.productservice.dto.login.response.LoginResponse;
import org.homanhquan.productservice.projection.UserInfoProjection;
import org.homanhquan.productservice.security.userDetails.CustomUserDetails;
import org.springframework.stereotype.Component;

@Component
public class LoginMapper {

    public LoginResponse toLoginResponse(CustomUserDetails userDetails, String token) {
        return LoginResponse.builder()
                .id(userDetails.getId())
                .token(token)
                .username(userDetails.getUsername())
                .fullName(userDetails.getFullName())
                .role(userDetails.getRole())
                .build();
    }
}
