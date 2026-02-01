package org.homanhquan.productservice.mapper;

import org.homanhquan.productservice.dto.orderItems.response.OrderItemsResponse;
import org.homanhquan.productservice.entity.OrderItems;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE // Reduces boilerplate code for "Mapping Ignore"
)
public interface OrderItemsMapper {
    // Entity -> DTO List
    @Mapping(target = "id", ignore = true)
    List<OrderItemsResponse> toDtoList(List<OrderItems> orderItems);
}
