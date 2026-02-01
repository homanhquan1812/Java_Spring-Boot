package org.homanhquan.productservice.mapper;

import org.homanhquan.productservice.projection.CartItemsProjection;
import org.homanhquan.productservice.dto.cartItems.response.CartItemsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE // Reduces boilerplate code for "Mapping Ignore"
)
public interface CartMapper {
    // Projection -> DTO List
    @Mapping(target = "id", ignore = true)
    List<CartItemsResponse> projectionToDtoList(List<CartItemsProjection> cartItemsProjections);
}
