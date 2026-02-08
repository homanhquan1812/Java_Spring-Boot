package org.homanhquan.productservice.annotation.swagger.auth;

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
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "Logout successful"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or expired token"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
})
public @interface ProtectedLogoutResponse {
}

