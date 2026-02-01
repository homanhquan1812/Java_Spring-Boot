package org.homanhquan.productservice.dto.product.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;

@Schema(
        name = "UpdateProductRequest",
        description = "Payload to update a product based on its ID",
        requiredProperties = {
                "name", "description", "price"
        }
)
@Builder // (Optional for testing)
public record UpdateProductRequest(
        @Size(max = 50, message = "Name must not exceed 50 characters")
        String name,

        String description,

        @DecimalMin(value = "1.00", inclusive = true, message = "Price must be at least 1")
        @Digits(integer = 16, fraction = 2)
        BigDecimal price
) {
}
