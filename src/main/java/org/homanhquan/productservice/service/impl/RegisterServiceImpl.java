package org.homanhquan.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.productservice.dto.register.request.UserRegisterRequest;
import org.homanhquan.productservice.dto.register.response.UserRegisterResponse;
import org.homanhquan.productservice.entity.Brand;
import org.homanhquan.productservice.entity.User;
import org.homanhquan.productservice.entity.UserInfo;
import org.homanhquan.productservice.event.register.UserRegisteredEvent;
import org.homanhquan.productservice.listener.register.UserRegisteredEventListener;
import org.homanhquan.productservice.service.RegisterService;
import org.homanhquan.productservice.service.helper.register.RegisterHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RegisterServiceImpl implements RegisterService {

    private final RegisterHelper registerHelper;
    private final UserRegisteredEventListener eventListener;

    @Override
    public UserRegisterResponse userRegister(UserRegisterRequest request) {
        log.info("Starting registration for user: {}", request.username());

        registerHelper.validateUserUniqueness(request);

        Brand brand = registerHelper.getBrandById(request.brandId());
        UserInfo userInfo = registerHelper.createUserInfo(request);
        User user = registerHelper.createUsers(userInfo, brand);
        registerHelper.createDefaultCart(user.getId());

        eventListener.onUserRegistered(
                new UserRegisteredEvent(request.email(), request.username())
        );

        log.info("User registered successfully: {}", request.username());

        return registerHelper.buildRegisterResponse(userInfo, brand);
    }
}