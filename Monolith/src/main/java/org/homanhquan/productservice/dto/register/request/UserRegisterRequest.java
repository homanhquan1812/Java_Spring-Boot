package org.homanhquan.productservice.dto.register.request;

import jakarta.validation.constraints.*;
import org.homanhquan.productservice.enums.Gender;

public record UserRegisterRequest(
        @NotBlank(message = "Full name is required")
        @Size(max = 50, message = "Full name must not exceed 50 characters")
        String fullName,

        @NotBlank(message = "Username is required")
        @Size(max = 50, message = "Username must not exceed 50 characters")
        @Pattern(
                regexp = "^[a-zA-Z0-9._-]+$",
                message = "Username only allows letters, digits, dot, underscore, and hyphen"
        )
        String username,

        @NotBlank(message = "Password is required")
        @Size(max = 100, message = "Password must not exceed 100 characters")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s]).{8,100}$",
                message = "Password must have lowercase, uppercase, digit and special character, length 8–100"
        )
        String password,

        @NotBlank(message = "Email is required")
        @Size(max = 50, message = "Email must not exceed 50 characters")
        @Email(message = "Email is invalid")
        String email,

        @NotBlank(message = "Phone is required")
        @Size(max = 10, message = "Phone must not exceed 10 characters")
        @Pattern(
                regexp = "^\\d{10}$",
                message = "Phone must be exactly 10 digits"
        )
        String phone,

        @NotNull(message = "Gender is required")
        Gender gender,

        @NotBlank(message = "Address is required")
        String address,

        @NotNull(message = "Brand ID is required")
        Long brandId
) {
}
