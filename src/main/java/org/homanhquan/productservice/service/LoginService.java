package org.homanhquan.productservice.service;

import org.homanhquan.productservice.dto.login.request.LoginRequest;
import org.homanhquan.productservice.dto.login.response.LoginResponse;

public interface LoginService {

    LoginResponse login(LoginRequest loginRequest);
}
