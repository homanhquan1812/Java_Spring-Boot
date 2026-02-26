package org.homanhquan.productservice.service;

import org.homanhquan.productservice.dto.login.request.LoginRequest;
import org.homanhquan.productservice.dto.login.response.LoginResponse;
import org.homanhquan.productservice.dto.logout.response.LogoutResponse;
import org.homanhquan.productservice.dto.register.request.UserRegisterRequest;
import org.homanhquan.productservice.dto.register.response.UserRegisterResponse;

public interface AuthService {

    LoginResponse login(LoginRequest loginRequest);
    LogoutResponse logout(String authorizationHeader);
    UserRegisterResponse userRegister(UserRegisterRequest request);
}
