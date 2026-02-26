package org.homanhquan.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.productservice.dto.login.request.LoginRequest;
import org.homanhquan.productservice.dto.login.response.LoginResponse;
import org.homanhquan.productservice.dto.logout.response.LogoutResponse;
import org.homanhquan.productservice.dto.register.request.UserRegisterRequest;
import org.homanhquan.productservice.dto.register.response.UserRegisterResponse;
import org.homanhquan.productservice.enums.Role;
import org.homanhquan.productservice.enums.Status;
import org.homanhquan.productservice.event.register.UserRegisteredEvent;
import org.homanhquan.productservice.exception.BadRequestException;
import org.homanhquan.productservice.exception.ResourceNotFoundException;
import org.homanhquan.productservice.projection.UserInfoProjection;
import org.homanhquan.productservice.repository.UserInfoRepository;
import org.homanhquan.productservice.security.jwt.JwtUtil;
import org.homanhquan.productservice.security.jwt.TokenBlacklistService;
import org.homanhquan.productservice.security.userDetails.CustomUserDetails;
import org.homanhquan.productservice.service.AuthService;
import org.homanhquan.productservice.service.helper.auth.login.AuthenticationHelper;
import org.homanhquan.productservice.service.helper.auth.login.LoginMapper;
import org.homanhquan.productservice.service.helper.auth.register.CreateCompleteUserHelper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationHelper authenticationHelper;
    private final LoginMapper loginMapper;
    private final UserInfoRepository userInfoRepository;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final CreateCompleteUserHelper createCompleteUserHelper;
    private final ApplicationEventPublisher applicationEventPublisher;

    private void validateUserUniqueness(UserRegisterRequest request) {
        if (userInfoRepository.existsByUsername(request.username()))
            throw new BadRequestException("Username already exists");
        if (userInfoRepository.existsByEmail(request.email()))
            throw new BadRequestException("Email already exists");
        if (userInfoRepository.existsByPhone(request.phone()))
            throw new BadRequestException("Phone number already exists");
    }
    /**
     * Flow:
     * 1. Validates user credentials via Spring Security.
     * 2. Retrieves user information from database.
     * 3. Generates JWT token with user details and role.
     * 4. Returns login response with token and user info.
     *
     * @param loginRequest
     * @return
     */
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        CustomUserDetails userDetails = authenticationHelper.authenticate(loginRequest);

        String token = jwtUtil.generateToken(userDetails);

        return loginMapper.toLoginResponse(userDetails, token);
    }

    @Override
    public LogoutResponse logout(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            tokenBlacklistService.blacklistToken(token);
        }

        return LogoutResponse.builder()
                .message("Logged out successfully.")
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Flow:
     * 1. Validate request uniqueness (username, email, phone).
     * 2. Create UserInfo, User, and default Cart.
     * 3. Publish event to send welcome email after transaction commits.
     * 4. Return response.
     *
     * @param request
     * @return
     */
    @Override
    @Transactional
    public UserRegisterResponse userRegister(UserRegisterRequest request) {
        validateUserUniqueness(request);

        createCompleteUserHelper.createCompleteUser(request);

        applicationEventPublisher.publishEvent(
                new UserRegisteredEvent(request.email(), request.username())
        );

        log.info("User registered successfully: {}", request.username());

        return UserRegisterResponse.builder()
                .message("User registered successfully.")
                .username(request.username())
                .email(request.email())
                .role(Role.USER)
                .status(Status.ACTIVE)
                .brandId(request.brandId())
                .build();
    }
}
