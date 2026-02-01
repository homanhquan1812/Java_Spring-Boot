package org.homanhquan.productservice.mapper;

import org.homanhquan.productservice.dto.order.request.UpdateOrderStatusRequest;
import org.homanhquan.productservice.dto.order.response.OrderResponse;
import org.homanhquan.productservice.entity.Order;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE, // Reduces boilerplate code for "Mapping Ignore"
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface OrderMapper {

    // Entity -> DTO
    @Mapping(target = "id", ignore = true)
    OrderResponse toDto(Order order);

    // Entity -> DTO List
    @Mapping(target = "id", ignore = true)
    List<OrderResponse> toDtoList(List<Order> orders);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(UpdateOrderStatusRequest updateOrderStatusRequest, @MappingTarget Order order);
}
