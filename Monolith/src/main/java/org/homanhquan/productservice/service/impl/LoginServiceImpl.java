package org.homanhquan.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.productservice.dto.login.request.LoginRequest;
import org.homanhquan.productservice.dto.login.response.LoginResponse;
import org.homanhquan.productservice.exception.ResourceNotFoundException;
import org.homanhquan.productservice.exception.UnauthorizedException;
import org.homanhquan.productservice.projection.LoginProjection;
import org.homanhquan.productservice.repository.LoginRepository;
import org.homanhquan.productservice.security.CustomUserDetails;
import org.homanhquan.productservice.security.JwtUtil;
import org.homanhquan.productservice.service.LoginService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginServiceImpl implements LoginService {

    private final LoginRepository loginRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil; // JWT

    /**
     * Authenticates user and generates JWT token with the following steps:
     * 1. Validates user credentials via Spring Security
     * 2. Retrieves user information from database
     * 3. Generates JWT token with user details and role
     * 4. Returns login response with token and user info
     *
     * @param loginRequest contains username and password
     * @return LoginResponse with JWT token, username, fullName, and role
     * @throws UnauthorizedException if credentials are invalid
     * @throws ResourceNotFoundException if user not found after authentication
     */
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        authenticateUser(loginRequest);
        LoginProjection user = getUserInfo(loginRequest.username());
        String token = generateToken(user);
        return buildLoginResponse(user, token);
    }

    private void authenticateUser(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.username(),
                            loginRequest.password()
                    )
            );
        } catch (BadCredentialsException e) {
            log.error("Login failed for user: {}", loginRequest.username());
            throw new UnauthorizedException("Invalid username or password");
        }
    }

    private LoginProjection getUserInfo(String username) {
        return loginRepository.findByUsernameAndRole(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    private String generateToken(LoginProjection user) {
        return jwtUtil.generateToken(
                CustomUserDetails.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .role(user.getRole())
                        .cartId(user.getCartId())
                        .build()
        );
    }

    private LoginResponse buildLoginResponse(LoginProjection user, String token) {
        return LoginResponse.builder()
                .id(user.getId())
                .cartId(user.getCartId())
                .token(token)
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }
}
