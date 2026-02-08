package org.homanhquan.productservice.annotation.swagger.crud;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

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
@Target({
        ElementType.METHOD,
        ElementType.ANNOTATION_TYPE
})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses({
        @ApiResponse(responseCode = "201", description = "Created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request body"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
})
@AuthApiResponse
public @interface PostApiResponse {
}
