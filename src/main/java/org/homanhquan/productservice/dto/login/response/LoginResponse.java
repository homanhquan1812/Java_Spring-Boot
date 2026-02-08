package org.homanhquan.productservice.dto.login.response;

import lombok.Builder;
import org.homanhquan.productservice.enums.Role;

import java.util.UUID;

@Builder
public record LoginResponse(
        UUID id,
        String token,
        String username,
        String fullName,
        Role role
) {
}
