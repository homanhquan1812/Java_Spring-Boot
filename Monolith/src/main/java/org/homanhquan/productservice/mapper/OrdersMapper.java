package org.homanhquan.productservice.mapper;

import org.homanhquan.productservice.dto.orders.request.UpdateOrderStatusRequest;
import org.homanhquan.productservice.dto.orders.response.OrdersResponse;
import org.homanhquan.productservice.entity.Orders;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE, // Reduces boilerplate code for "Mapping Ignore"
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface OrdersMapper {

    // Entity -> DTO
    @Mapping(target = "id", ignore = true)
    OrdersResponse toDto(Orders orders);

    // Entity -> DTO List
    @Mapping(target = "id", ignore = true)
    List<OrdersResponse> toDtoList(List<Orders> orders);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(UpdateOrderStatusRequest updateOrderStatusRequest, @MappingTarget Orders orders);
}
