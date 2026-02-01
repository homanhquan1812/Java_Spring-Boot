package org.homanhquan.authservice.service;

import org.homanhquan.authservice.dto.login.request.LoginRequest;
import org.homanhquan.authservice.dto.login.response.LoginResponse;

public interface LoginService {

    LoginResponse login(LoginRequest loginRequest);
}
