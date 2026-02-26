package org.homanhquan.productservice.service.helper.auth.register;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.productservice.dto.register.request.UserRegisterRequest;
import org.homanhquan.productservice.entity.Cart;
import org.homanhquan.productservice.entity.User;
import org.homanhquan.productservice.entity.UserInfo;
import org.homanhquan.productservice.exception.ResourceNotFoundException;
import org.homanhquan.productservice.repository.BrandRepository;
import org.homanhquan.productservice.repository.CartRepository;
import org.homanhquan.productservice.repository.UserInfoRepository;
import org.homanhquan.productservice.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateCompleteUserHelper {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final UserInfoRepository userInfoRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final BrandRepository brandRepository;

    public void createCompleteUser(UserRegisterRequest request) {
        brandRepository.findById(request.brandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found: " + request.brandId()));

        UserInfo userInfo = UserInfo.of(
                request.fullName(),
                request.username(),
                passwordEncoder.encode(request.password()),
                request.email(),
                request.phone(),
                request.gender(),
                request.address()
        );
        userInfoRepository.save(userInfo);

        log.info("UserInfo saved for user: {}", request.username());

        User user = User.of(
                userInfo.getId(),
                request.brandId()
        );
        userRepository.save(user);

        log.info("Users entity saved for user: {}", userInfo.getUsername());

        Cart cart = Cart.of(
                user.getId()
        );
        cartRepository.save(cart);

        log.info("Default cart created for userId: {}", user.getId());
    }
}
