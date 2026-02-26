package org.homanhquan.productservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.homanhquan.productservice.annotation.swagger.auth.ProtectedLogoutResponse;
import org.homanhquan.productservice.annotation.swagger.auth.PublicLoginResponse;
import org.homanhquan.productservice.annotation.swagger.auth.PublicRegisterResponse;
import org.homanhquan.productservice.dto.login.request.LoginRequest;
import org.homanhquan.productservice.dto.login.response.LoginResponse;
import org.homanhquan.productservice.dto.logout.response.LogoutResponse;
import org.homanhquan.productservice.dto.register.request.UserRegisterRequest;
import org.homanhquan.productservice.dto.register.response.UserRegisterResponse;
import org.homanhquan.productservice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring Boot uses Jackson to convert data between Java objects and JSON in controllers. Specifically:
 * - Serialization: Java object → JSON.
 * - Deserialization: JSON → Java object.
 * ==================================================
 * Annotation definition:
 * - @Tag: Groups related API endpoints in Swagger documentation.
 * - @RequiredArgsConstructor: Automatically injects dependencies for constructor.
 * - @Validated: Enables validation of method parameters (e.g., @Min, @Max) at the controller level.
 * - @Controller: A bean for the web layer (MVC) to return views (like JSP, Thymeleaf templates).
 * - @RestController: A specialized @Controller that combines @Controller and @ResponseBody, automatically serializing return values to JSON/XML for REST APIs.
 * - @Controller: A bean for the web layer (MVC) to return views (like JSP, Thymeleaf templates).
 * - @RequestMapping("/api"): Defines base URL starting with /api.
 * - @PathVariable: Extract path variables.
 * - @RequestBody: Convert JSON to object.
 * - @RequestParam: Extract query parameters.
 * - ResponseEntity<T>: Represents HTTP response with body, status, and headers. No need to use it when:
 *   + The HTTP status is fixed (e.g. 200 OK, 201 CREATED).
 *   + No custom HTTP headers are required.
 *   + Errors are handled centrally using @ControllerAdvice.
 * - @Valid: Triggers validation on request body or method parameters using Bean Validation constraints (e.g. @NotNull, @Size).
 * - @AuthenticationPrincipal(expression = "id") UUID userId: Resolves the current user ID from authentication. Useful for logging, or avoiding /users/{id} pattern.
 *   If additional user information is required, you can inject the full principal: @AuthenticationPrincipal CustomUserDetails userDetails.
 *   Then extract fields as needed, for example:
 *   + UUID userId = userDetails.getId();
 *   + String username = userDetails.getUsername();
 *   In most cases, using @AuthenticationPrincipal(expression = "id") is sufficient.
 * - @Deprecated(since = "1.0", forRemoval = true): This method will be removed in the future since version 1.0.
 * ==================================================
 * Difference between PUT vs PATCH:
 * - Not much difference between performance and syntax.
 * - They still need NullValuePropertyMappingStrategy.IGNORE in Mapper to keep old data if no update for some fields.
 * - Since a lot of methods just update partial data, PATCH is more common than PUT.
 */
@Tag(name = "Authentication", description = "Authentication APIs")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // [POST] /auth/login
    @Operation(summary = "User login")
    @PublicLoginResponse
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    // [POST] /auth/logout
    @Operation(summary = "User logout")
    @ProtectedLogoutResponse
    @PostMapping("/logout")
    public LogoutResponse logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return authService.logout(authHeader);
    }

    // [POST] /auth/register/user
    @Operation(summary = "User registration")
    @PublicRegisterResponse
    @PostMapping("/register/user")
    @ResponseStatus(HttpStatus.CREATED)
    public UserRegisterResponse registerUser(
            @Valid @RequestBody UserRegisterRequest userRegisterRequest) {
        return authService.userRegister(userRegisterRequest);
    }
}
