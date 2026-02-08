package org.homanhquan.productservice.dto.register.response;

import lombok.Builder;
import org.homanhquan.productservice.enums.Role;
import org.homanhquan.productservice.enums.Status;

@Builder
public record UserRegisterResponse(
        String username,
        String email,
        String message,
        Role role,
        Status status,
        Long brandId
) {
}
