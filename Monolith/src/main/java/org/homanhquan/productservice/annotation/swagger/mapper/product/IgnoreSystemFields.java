package org.homanhquan.productservice.annotation.swagger.mapper.product;

import org.mapstruct.Mapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation explanation:
 * - @Target(ElementType.METHOD): This annotation can only be applied to methods.
 *   @Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE }): This annotation can be applied to methods and other annotations.
 * - @Retention(RetentionPolicy.RUNTIME): This annotation is retained at runtime and can be accessed via reflection.
 *   @Retention(RetentionPolicy.CLASS): This annotation is retained in the compiled class file but not available at runtime.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
@Mapping(target = "id", ignore = true)
@Mapping(target = "version", ignore = true)
@Mapping(target = "createdAt", ignore = true)
@Mapping(target = "updatedAt", ignore = true)
@Mapping(target = "deletedAt", ignore = true)
@Mapping(target = "createdBy", ignore = true)
@Mapping(target = "updatedBy", ignore = true)
@Mapping(target = "deletedBy", ignore = true)
@Mapping(target = "brand", ignore = true)
public @interface IgnoreSystemFields {
}
