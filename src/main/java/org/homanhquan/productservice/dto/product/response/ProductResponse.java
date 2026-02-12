package org.homanhquan.productservice.dto.product.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import org.homanhquan.productservice.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonPropertyOrder({
        "id", "name", "brandName", "description", "price", "status",
        "createdAt", "createdBy", "updatedAt", "updatedBy", "deletedAt", "deletedBy"
}) // Sorts JSON rows in order (Optional)
@Builder
public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String brandName,
        Status status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt,
        UUID createdBy,
        UUID updatedBy,
        UUID deletedBy,
        Long version
) {
}
