package org.homanhquan.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.productservice.dto.login.request.LoginRequest;
import org.homanhquan.productservice.dto.login.response.LoginResponse;
import org.homanhquan.productservice.exception.ResourceNotFoundException;
import org.homanhquan.productservice.exception.UnauthorizedException;
import org.homanhquan.productservice.projection.LoginProjection;
import org.homanhquan.productservice.service.LoginService;
import org.homanhquan.productservice.service.helper.login.LoginHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginServiceImpl implements LoginService {

    private final LoginHelper loginHelper;

    /**
     * Authenticates user and generates JWT token with the following steps:
     * 1. Validates user credentials via Spring Security.
     * 2. Retrieves user information from database.
     * 3. Generates JWT token with user details and role.
     * 4. Returns login response with token and user info.
     *
     * @param loginRequest contains username and password
     * @return LoginResponse with JWT token, username, fullName, and role
     * @throws UnauthorizedException if credentials are invalid
     * @throws ResourceNotFoundException if user not found after authentication
     */
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        loginHelper.authenticateUser(loginRequest);
        LoginProjection user = loginHelper.getUserInfo(loginRequest.username());
        String token = loginHelper.generateToken(user);
        return loginHelper.buildLoginResponse(user, token);
    }
}
