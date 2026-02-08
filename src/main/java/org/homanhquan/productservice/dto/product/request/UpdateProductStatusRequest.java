package org.homanhquan.productservice.dto.product.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.homanhquan.productservice.enums.Status;

@Schema(
        name = "CreateProductsRequest",
        description = "Payload to update a product's status based on its ID",
        requiredProperties = {
                "status"
        }
)
@Builder // (Optional for testing)
public record UpdateProductStatusRequest(
        Status status
) {
}
