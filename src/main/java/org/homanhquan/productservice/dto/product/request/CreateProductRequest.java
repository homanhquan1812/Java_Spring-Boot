package org.homanhquan.productservice.dto.product.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;

/**
 * Annotation explanation:
 * - @DecimalMin(value = "0.0", inclusive = false): Validate that a numeric value is greater than or equal to a specified minimum decimal value:
 *   + If inclusive = false, numbers like 0.00, 0.0000 or less than 0 are not allowed.
 *   + If inclusive = true, numbers like 0.00, 0.0000 are allowed, still less than 0 are not allowed.
 * - @Digits(integer = 16, fraction = 2): Validates that a number has at most 16 digits before the decimal point
 *   and at most 2 digits after the decimal point (e.g. 9999999999999999.99 (OK), 100.5 (NO), 100.999 (NO)).
 * - @Size: Validate the size or length of a value, such as strings, collections, maps, or arrays, by specifying minimum and/or maximum limits:
 *   + min = 5 -> Minimum text length of value.
 *   + max = 10 -> Maximum text length of value.
 */
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
        @Size(min = 1, max = 50, message = "Name must not exceed 50 characters")
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
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        @Digits(integer = 16, fraction = 2, message = "Price format is invalid")
        BigDecimal price,

        @Schema(
                description = "Product's brand ID (foreign key)",
                example = "101"
        )
        @NotNull(message = "Brand ID is required")
        Long brandId
) {
}
