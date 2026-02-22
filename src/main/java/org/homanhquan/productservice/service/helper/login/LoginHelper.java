package org.homanhquan.productservice.service.helper.login;

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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginHelper {

    private final LoginRepository loginRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil; // JWT

    public void authenticateUser(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.username(),
                            loginRequest.password()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Invalid username or password");
        }
    }

    public LoginProjection getUserInfo(String username) {
        return loginRepository.findByUsernameAndRole(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    public String generateToken(LoginProjection user) {
        return jwtUtil.generateToken(
                CustomUserDetails.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .role(user.getRole())
                        .build()
        );
    }

    public LoginResponse buildLoginResponse(LoginProjection user, String token) {
        return LoginResponse.builder()
                .id(user.getId())
                .token(token)
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }
}
