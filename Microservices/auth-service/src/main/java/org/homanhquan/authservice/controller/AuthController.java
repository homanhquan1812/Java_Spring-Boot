package org.homanhquan.authservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.homanhquan.authservice.dto.login.request.LoginRequest;
import org.homanhquan.authservice.dto.login.response.LoginResponse;
import org.homanhquan.authservice.dto.logout.response.LogoutResponse;
import org.homanhquan.authservice.dto.register.request.UserRegisterRequest;
import org.homanhquan.authservice.dto.register.response.UserRegisterResponse;
import org.homanhquan.authservice.service.LoginService;
import org.homanhquan.authservice.service.LogoutService;
import org.homanhquan.authservice.service.RegisterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;
    private final LogoutService logoutService;
    private final RegisterService registerService;

    // [POST] /auth/login
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        return loginService.login(loginRequest);
    }

    // [POST] /auth/logout
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public LogoutResponse logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return logoutService.logout(authHeader);
    }

    // [POST] /auth/register/user
    @PostMapping("/register/user")
    public ResponseEntity<UserRegisterResponse> registerUser(
            @Valid @RequestBody UserRegisterRequest userRegisterRequest) {
        UserRegisterResponse response = registerService.userRegister(userRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
