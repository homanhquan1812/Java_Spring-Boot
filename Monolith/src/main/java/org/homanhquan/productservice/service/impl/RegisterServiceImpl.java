package org.homanhquan.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.productservice.dto.register.request.UserRegisterRequest;
import org.homanhquan.productservice.dto.register.response.UserRegisterResponse;
import org.homanhquan.productservice.entity.Brand;
import org.homanhquan.productservice.entity.User;
import org.homanhquan.productservice.entity.UserInfo;
import org.homanhquan.productservice.service.RegisterService;
import org.homanhquan.productservice.service.helper.RegisterHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RegisterServiceImpl implements RegisterService {

    private final RegisterHelper registerHelper;

    @Override
    public UserRegisterResponse userRegister(UserRegisterRequest userRegisterRequest) {
        log.info("Starting registration for user: {}", userRegisterRequest.username());

        registerHelper.validateUserUniqueness(userRegisterRequest);
        Brand brand = registerHelper.getBrandById(userRegisterRequest.brandId());
        UserInfo userInfo = registerHelper.createUserInfo(userRegisterRequest);
        User user = registerHelper.createUsers(userInfo, brand);
        registerHelper.createDefaultCart(user.getId());
        registerHelper.sendWelcomeEmail(userRegisterRequest);

        log.info("User registered successfully: {}", userRegisterRequest.username());

        return registerHelper.buildRegisterResponse(userInfo, brand);
    }
}