package org.homanhquan.productservice.mapper;

import org.homanhquan.productservice.dto.cartItem.request.CreateCartItemsRequest;
import org.homanhquan.productservice.dto.cartItem.response.CartItemResponse;
import org.homanhquan.productservice.entity.CartItem;
import org.homanhquan.productservice.projection.CartItemProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE // Reduces boilerplate code for "Mapping Ignore"
)
public interface CartItemMapper {

    // Entity -> DTO
    CartItemResponse toDto(CartItem cartItem);

    // DTO -> Entity
    @Mapping(target = "id", ignore = true)
    CartItem toEntity(CreateCartItemsRequest request);

    @Mapping(target = "id", ignore = true)
    List<CartItemResponse> projectionToDtoList(List<CartItemProjection> cartItemProjections);
}
