package org.homanhquan.productservice.mapper;

import org.homanhquan.productservice.dto.product.request.CreateProductRequest;
import org.homanhquan.productservice.dto.product.request.UpdateProductRequest;
import org.homanhquan.productservice.dto.product.request.UpdateProductStatusRequest;
import org.homanhquan.productservice.dto.product.response.ProductResponse;
import org.homanhquan.productservice.entity.Product;
import org.homanhquan.productservice.enums.Status;
import org.homanhquan.productservice.projection.ProductProjection;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.WARN,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        imports = {
                Status.class
        }
)
public interface ProductMapper {
    // Entity -> DTO (Read)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "brandName", ignore = true)
    ProductResponse toDto(Product product);

    // Entity -> DTO (Read)
    List<ProductResponse> projectionToDtoList(List<ProductProjection> productProjections);

    // DTO -> Entity (Create)
    @Mapping(target = "status", expression = "java(Status.ACTIVE)")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "brand", ignore = true)
    Product toEntity(CreateProductRequest createProductRequest);

    // Projection -> DTO (Read)
    ProductResponse projectionToDto(ProductProjection productProjection);

    // DTO -> Entity (Update)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "brand", ignore = true)
    void updateEntityFromDto(UpdateProductRequest updateProductRequest, @MappingTarget Product product);

    // DTO -> Entity (Update)
    @Mapping(target = "id", ignore = true) // Ignore mapping for ID (not for updates)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "brand", ignore = true)
    void updateEntityFromDtoForStatus(UpdateProductStatusRequest updateProductStatusRequest, @MappingTarget Product product);
}
