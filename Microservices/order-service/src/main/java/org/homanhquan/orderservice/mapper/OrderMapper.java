package org.homanhquan.orderservice.mapper;

import org.homanhquan.orderservice.dto.order.request.UpdateOrderStatusRequest;
import org.homanhquan.orderservice.dto.order.response.OrderResponse;
import org.homanhquan.orderservice.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE, // Reduces boilerplate code for "Mapping Ignore"
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface OrderMapper {

    // Entity -> DTO
    OrderResponse toDto(Order order);

    // Entity -> DTO List
    List<OrderResponse> toDtoList(List<Order> orders);

    void updateEntityFromDto(UpdateOrderStatusRequest updateOrderStatusRequest, @MappingTarget Order order);
}
