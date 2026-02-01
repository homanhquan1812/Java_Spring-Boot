package org.homanhquan.authservice.service;

import org.homanhquan.authservice.dto.register.request.UserRegisterRequest;
import org.homanhquan.authservice.dto.register.response.UserRegisterResponse;

public interface RegisterService {

    UserRegisterResponse userRegister(UserRegisterRequest userRegisterRequest);
}
