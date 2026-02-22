package org.homanhquan.productservice.service.helper.register;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.productservice.dto.register.request.UserRegisterRequest;
import org.homanhquan.productservice.dto.register.response.UserRegisterResponse;
import org.homanhquan.productservice.entity.Brand;
import org.homanhquan.productservice.entity.Cart;
import org.homanhquan.productservice.entity.User;
import org.homanhquan.productservice.entity.UserInfo;
import org.homanhquan.productservice.enums.Role;
import org.homanhquan.productservice.enums.Status;
import org.homanhquan.productservice.exception.BadRequestException;
import org.homanhquan.productservice.exception.ResourceNotFoundException;
import org.homanhquan.productservice.mapper.UserInfoMapper;
import org.homanhquan.productservice.mapper.UserMapper;
import org.homanhquan.productservice.repository.*;
import org.homanhquan.productservice.service.EmailService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegisterHelper {

    private final LoginRepository loginRepository;
    private final RegisterRepository registerRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final UserInfoRepository userInfoRepository;
    private final BrandRepository brandRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserInfoMapper userInfoMapper;
    private final UserMapper userMapper;
    private final EmailService emailService;

    public void validateUserUniqueness(UserRegisterRequest userRegisterRequest) {
        if (loginRepository.findByUsernameAndRole(userRegisterRequest.username()).isPresent()) {
            throw new BadRequestException("Username already exists");
        }
        if (registerRepository.findByEmail(userRegisterRequest.email()).isPresent()) {
            throw new BadRequestException("Email already exists");
        }
    }

    public Brand getBrandById(Long brandId) {
        return brandRepository.findById(brandId)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found: " + brandId));
    }

    public UserInfo createUserInfo(UserRegisterRequest userRegisterRequest) {
        UserInfo userInfo = userInfoMapper.fromUserRegisterRequest(userRegisterRequest);
        userInfo.setPassword(passwordEncoder.encode(userRegisterRequest.password()));
        UserInfo savedUserInfo = userInfoRepository.save(userInfo);

        log.info("UserInfo saved for user: {}", userRegisterRequest.username());

        return savedUserInfo;
    }

    public User createUsers(UserInfo userInfo, Brand brand) {
        User user = userMapper.toUsersFromUserInfoAndBrand(userInfo, brand);
        User savedUser = userRepository.save(user);

        log.info("Users entity saved for user: {}", userInfo.getUsername());

        return savedUser;
    }

    public void createDefaultCart(UUID userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);

        log.info("Default cart created for userId: {}", userId);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendWelcomeEmail(UserRegisterRequest userRegisterRequest) {
        emailService.sendWelcomeEmail(userRegisterRequest.email(), userRegisterRequest.username());

        log.info("Email has been sent to: {}", userRegisterRequest.email());
    }

    public UserRegisterResponse buildRegisterResponse(UserInfo userInfo, Brand brand) {
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
