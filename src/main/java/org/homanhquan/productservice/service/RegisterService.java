package org.homanhquan.productservice.service;

import org.homanhquan.productservice.dto.register.request.UserRegisterRequest;
import org.homanhquan.productservice.dto.register.response.UserRegisterResponse;

public interface RegisterService {

    UserRegisterResponse userRegister(UserRegisterRequest userRegisterRequest);
}
