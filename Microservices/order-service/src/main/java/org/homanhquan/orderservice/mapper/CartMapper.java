package org.homanhquan.orderservice.mapper;

import org.homanhquan.orderservice.projection.CartItemProjection;
import org.homanhquan.orderservice.dto.cartItem.response.CartItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE // Reduces boilerplate code for "Mapping Ignore"
)
public interface CartMapper {
    // Projection -> DTO List
    List<CartItemResponse> projectionToDtoList(List<CartItemProjection> cartItemProjections);
}
