package org.homanhquan.productservice.mapper;

import org.homanhquan.productservice.annotation.swagger.mapper.product.IgnoreSystemFields;
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
 * ==================================================
 * Pattern: Target to<Target>(Source source)
 *            |        |        |      |
 *         UserDto   toDto    (User  user)
 *
 * UserDto: Target DTO type returned by the mapper.
 * toDto(User user): Create a new target object and copy data from source.
 *
 * In case there are some mappers for different roles such as admins or users:
 * Set their name like this: toAdminDto, toUserDto, etc.
 * ==================================================
 * Annotation explanation:
 * - @Mapper: Marks an interface as a mapper.
 *   + componentModel = "spring": Makes MapStruct work with Spring's dependency injection.
 *   + unmappedTargetPolicy = ReportingPolicy.WARN: Helps reduce boilerplate @Mapping(target = "...", ignore = true) for createdBy, createdAt, etc.
 *     There are 3 levels: IGNORE, WARN, and ERROR. IGNORE is not recommended for production.
 *   + nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE: Only updates non-null fields; null fields remain unchanged.
 *   + imports = { Status.class }: Imports enums (or constants, classes).
 * - @Mapping(...): Defines mapping rules between source and target properties.
 *   + target = "...": Refers to a property of the destination object.
 *   + expression = "...": Assigns a value using a Java expression (Enums, constants, etc).
 *   + source = "...": Specifies the source property path when it differs from the target property name, commonly used when mapping from multiple source objects (entities).
 *     For example: @Mapping(target = "brandId", source = "brand.id") // Copy id from brand, assign it to brandId in User entity
 *     CAUTION: If the entity uses LAZY loading and the relation is NOT pre-fetched (via JOIN FETCH or Projection), accessing nested properties will trigger additional queries (N+1 problem).
 *   + ignore = true: Skips mapping for the specified target property.
 * - @MappingTarget(...): Update an existing object instead of creating a new one.
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.WARN,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        imports = {
                Status.class
        },
        builder = @Builder(disableBuilder = true)
)
public interface ProductMapper {
    /**
     * Entity -> DTO (READ)
     */
    @Mapping(target = "brandName", source = "brand.name")
    ProductResponse toDto(Product product);

    /**
     * Projection -> DTO (READ)
     */
    ProductResponse projectionToDto(ProductProjection productProjection);
    List<ProductResponse> projectionToDtoList(List<ProductProjection> productProjections);

    /**
     * DTO -> Entity (CREATE)
     */
    @IgnoreSystemFields
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "status", ignore = true)
    Product toEntity(CreateProductRequest createProductRequest);

    /**
     * DTO -> Entity (UPDATE)
     */
    @IgnoreSystemFields
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntityFromDto(UpdateProductRequest updateProductRequest, @MappingTarget Product product);

    @IgnoreSystemFields
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateStatusFromDto(UpdateProductStatusRequest request, @MappingTarget Product product);
}
