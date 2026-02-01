package org.homanhquan.orderservice.mapper;

import org.homanhquan.orderservice.dto.orderItem.response.OrderItemResponse;
import org.homanhquan.orderservice.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE // Reduces boilerplate code for "Mapping Ignore"
)
public interface OrderItemMapper {
    // Entity -> DTO List
    List<OrderItemResponse> toDtoList(List<OrderItem> orderItem);
}
