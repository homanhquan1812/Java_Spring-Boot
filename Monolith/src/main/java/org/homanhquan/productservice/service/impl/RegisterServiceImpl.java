package org.homanhquan.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.productservice.dto.register.request.UserRegisterRequest;
import org.homanhquan.productservice.dto.register.response.UserRegisterResponse;
import org.homanhquan.productservice.entity.Brand;
import org.homanhquan.productservice.entity.Cart;
import org.homanhquan.productservice.entity.UserInfo;
import org.homanhquan.productservice.entity.Users;
import org.homanhquan.productservice.enums.Role;
import org.homanhquan.productservice.enums.Status;
import org.homanhquan.productservice.exception.BadRequestException;
import org.homanhquan.productservice.exception.ResourceNotFoundException;
import org.homanhquan.productservice.mapper.UserInfoMapper;
import org.homanhquan.productservice.mapper.UsersMapper;
import org.homanhquan.productservice.repository.BrandRepository;
import org.homanhquan.productservice.repository.CartRepository;
import org.homanhquan.productservice.repository.LoginRepository;
import org.homanhquan.productservice.repository.RegisterRepository;
import org.homanhquan.productservice.repository.UserInfoRepository;
import org.homanhquan.productservice.repository.UsersRepository;
import org.homanhquan.productservice.service.EmailService;
import org.homanhquan.productservice.service.RegisterService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RegisterServiceImpl implements RegisterService {

    private final LoginRepository loginRepository;
    private final RegisterRepository registerRepository;
    private final CartRepository cartRepository;
    private final UsersRepository usersRepository;
    private final UserInfoRepository userInfoRepository;
    private final BrandRepository brandRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserInfoMapper userInfoMapper;
    private final UsersMapper usersMapper;
    private final EmailService emailService;

    @Override
    public UserRegisterResponse userRegister(UserRegisterRequest userRegisterRequest) {
        log.info("Starting registration for user: {}", userRegisterRequest.username());

        validateUserUniqueness(userRegisterRequest);
        Brand brand = getBrandById(userRegisterRequest.brandId());
        UserInfo userInfo = createUserInfo(userRegisterRequest);
        Users users = createUsers(userInfo, brand);
        createDefaultCart(users.getId());
        sendWelcomeEmail(userRegisterRequest);

        log.info("User registered successfully: {}", userRegisterRequest.username());

        return buildRegisterResponse(userInfo, brand);
    }

    private void validateUserUniqueness(UserRegisterRequest userRegisterRequest) {
        if (loginRepository.findByUsernameAndRole(userRegisterRequest.username()).isPresent()) {
            throw new BadRequestException("Username already exists");
        }
        if (registerRepository.findByEmail(userRegisterRequest.email()).isPresent()) {
            throw new BadRequestException("Email already exists");
        }
    }

    private Brand getBrandById(Long brandId) {
        return brandRepository.findById(brandId)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found: " + brandId));
    }

    private UserInfo createUserInfo(UserRegisterRequest userRegisterRequest) {
        UserInfo userInfo = userInfoMapper.fromUserRegisterRequest(userRegisterRequest);
        userInfo.setPassword(passwordEncoder.encode(userRegisterRequest.password()));
        UserInfo savedUserInfo = userInfoRepository.save(userInfo);

        log.info("UserInfo saved for user: {}", userRegisterRequest.username());
        return savedUserInfo;
    }

    private Users createUsers(UserInfo userInfo, Brand brand) {
        Users users = usersMapper.toUsersFromUserInfoAndBrand(userInfo, brand);
        Users savedUsers = usersRepository.save(users);

        log.info("Users entity saved for user: {}", userInfo.getUsername());
        return savedUsers;
    }

    private void createDefaultCart(UUID userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);

        log.info("Default cart created for userId: {}", userId);
    }

    private void sendWelcomeEmail(UserRegisterRequest userRegisterRequest) {
        emailService.sendWelcomeEmail(userRegisterRequest.email(), userRegisterRequest.username());

        log.info("Email has been sent to: {}", userRegisterRequest.email());
    }

    private UserRegisterResponse buildRegisterResponse(UserInfo userInfo, Brand brand) {
        return UserRegisterResponse.builder()
                .message("User registered successfully")
                .username(userInfo.getUsername())
                .email(userInfo.getEmail())
                .role(Role.USER)
                .status(Status.ACTIVE)
                .brandId(brand.getId())
                .build();
    }
}