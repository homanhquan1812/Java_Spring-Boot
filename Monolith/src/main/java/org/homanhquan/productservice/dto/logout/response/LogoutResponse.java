package org.homanhquan.productservice.dto.logout.response;

import java.time.LocalDateTime;

public record LogoutResponse(
        String message,
        LocalDateTime timestamp
) {
}
