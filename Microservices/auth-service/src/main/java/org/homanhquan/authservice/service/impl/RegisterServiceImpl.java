package org.homanhquan.authservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.authservice.dto.register.request.UserRegisterRequest;
import org.homanhquan.authservice.dto.register.response.UserRegisterResponse;
import org.homanhquan.authservice.entity.Brand;
import org.homanhquan.authservice.entity.Cart;
import org.homanhquan.authservice.entity.UserInfo;
import org.homanhquan.authservice.entity.Users;
import org.homanhquan.authservice.enums.Role;
import org.homanhquan.authservice.enums.Status;
import org.homanhquan.authservice.exception.BadRequestException;
import org.homanhquan.authservice.exception.ResourceNotFoundException;
import org.homanhquan.authservice.mapper.UserInfoMapper;
import org.homanhquan.authservice.mapper.UsersMapper;
import org.homanhquan.authservice.repository.*;
import org.homanhquan.authservice.service.EmailService;
import org.homanhquan.authservice.service.RegisterService;
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

    /**
     * Registers a new user account with the following steps:
     * 1. Validates username and email uniqueness
     * 2. Creates user account with encrypted password
     * 3. Associates user with specified brand
     * 4. Initializes empty shopping cart
     * 5. Sends welcome email asynchronously via RabbitMQ
     *
     * @param userRegisterRequest contains username, email, password, and brandId
     * @return UserRegisterResponse with registration status
     * @throws BadRequestException if username or email already exists
     * @throws ResourceNotFoundException if brandId is invalid
     */
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
        Cart cart = Cart.builder()
                .userId(userId)
                .totalPrice(BigDecimal.ZERO)
                .build();
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