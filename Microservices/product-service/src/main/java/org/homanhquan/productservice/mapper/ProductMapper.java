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

/**
 * Mapper is a utility that converts data between different object types:
 * - Entity → DTO (for responses).
 * - DTO → Entity (for database operations).
 * Using a mapper reduces boilerplate code and makes the code cleaner, safer, and easier to maintain.
 */
@Mapper(
        componentModel = "spring", // Makes MapStruct work with Spring's dependency injection
        unmappedTargetPolicy = ReportingPolicy.WARN,
        /**
         * Helps reduce boilerplate @Mapping(target = "...", ignore = true) for createdBy, createdAt, etc.
         * There are 3 levels: IGNORE, WARN, and ERROR. IGNORE is not recommended for production.
        */
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, // Only updates non-null fields; null fields remain unchanged
        imports = {
                Status.class
        } // Imports enums (or constants, classes)
) // Marks an interface for MapStruct to generate the mapping implementation
public interface ProductMapper {
    /**
     * Pattern: Target to<Target>(Source source)
     *            |        |        |      |
     *         UserDto   toDto    (User  user)
     *
     * UserDto: Target DTO type returned by the mapper.
     * toDto(User user): Create a new target object and copy data from source.
     *
     * In case there are some mappers for different roles such as admins or users:
     * Set their name like this: toAdminDto, toUserDto, etc.
     *
     * Other definitions:
     * - @Mapping(...): Defines mapping rules between source and target properties.
     * - target = "...": Refers to a property of the destination object.
     * - expression = "...": Assigns a value using a Java expression (Enums, constants, etc).
     * - source = "...": Specifies the source property path when it differs from the target property name, commonly used when mapping from multiple source objects (entities).
     *   For example: @Mapping(target = "brandId", source = "brand.id") // Copy id from brand, assign it to brandId in User entity
     *   -> Avoid mapping nested relations (e.g. brand.name) in list APIs. Since brand is LAZY → accessing brand.getName() may cause N+1 queries.
     * - ignore = true: Skips mapping for the specified target property.
     * - @MappingTarget(...): Update an existing object instead of creating a new one.
     */
    // Entity -> DTO (Read)
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
    void updateEntityFromDtoForStatus(UpdateProductStatusRequest updateProductStatusRequest, @MappingTarget Product product);
}
