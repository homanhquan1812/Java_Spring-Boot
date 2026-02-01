package org.homanhquan.productservice.mapper;

import org.homanhquan.productservice.dto.cartItems.request.CreateCartItemsRequest;
import org.homanhquan.productservice.dto.cartItems.response.CartItemsResponse;
import org.homanhquan.productservice.entity.CartItems;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE // Reduces boilerplate code for "Mapping Ignore"
)
public interface CartItemsMapper {

    // Entity -> DTO
    CartItemsResponse toDto(CartItems cartItems);

    // DTO -> Entity
    @Mapping(target = "id", ignore = true)
    CartItems toEntity(CreateCartItemsRequest createCartItemsRequest);
}
