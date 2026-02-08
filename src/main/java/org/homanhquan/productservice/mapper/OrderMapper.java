package org.homanhquan.productservice.mapper;

import org.homanhquan.productservice.dto.order.request.UpdateOrderStatusRequest;
import org.homanhquan.productservice.dto.order.response.OrderResponse;
import org.homanhquan.productservice.entity.Order;
import org.mapstruct.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    OrderResponse toDtoPage(Order order);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(UpdateOrderStatusRequest updateOrderStatusRequest, @MappingTarget Order order);
}
