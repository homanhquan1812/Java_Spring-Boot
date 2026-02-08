package org.homanhquan.productservice.dto.product.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;

@Schema(
        name = "CreateProductsRequest",
        description = "Payload to create a new product",
        requiredProperties = {
                "name", "description", "price", "brandId"
        }
)
@Builder // (Optional for testing)
public record CreateProductRequest(
        @Schema(
                description = "Product name",
                example = "iPhone 15 Pro",
                maxLength = 50
        )
        @NotBlank(message = "Name is required")
        @Size(max = 50, message = "Name must not exceed 50 characters")
        String name,

        @Schema(
                description = "Product description",
                example = "Flagship 2025, titanium frame"
        )
        @NotBlank(message = "Description is required")
        String description,

        @Schema(
                description = "Product price (>= 1)",
                example = "25990000",
                minimum = "1"
        )
        @NotNull(message = "Price is required")
        @DecimalMin(value = "1.00", inclusive = true, message = "Price must be at least 1")
        BigDecimal price,

        @Schema(
                description = "Product's brand ID (foreign key)",
                example = "101"
        )
        @NotNull(message = "Brand ID is required")
        Long brandId
) {
}
