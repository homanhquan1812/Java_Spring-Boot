package org.homanhquan.productservice.dto.logout.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record LogoutResponse(
        String message,
        LocalDateTime timestamp
) {
}
