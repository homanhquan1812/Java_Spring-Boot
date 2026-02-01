package org.homanhquan.orderservice.mapper;

import org.homanhquan.orderservice.dto.cartItem.request.CreateCartItemRequest;
import org.homanhquan.orderservice.dto.cartItem.response.CartItemResponse;
import org.homanhquan.orderservice.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE // Reduces boilerplate code for "Mapping Ignore"
)
public interface CartItemMapper {

    // Entity -> DTO
    CartItemResponse toDto(CartItem cartItem);

    // DTO -> Entity
    @Mapping(target = "id", ignore = true)
    CartItem toEntity(CreateCartItemRequest createCartItemRequest);
}
