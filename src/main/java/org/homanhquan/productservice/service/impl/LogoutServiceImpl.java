package org.homanhquan.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.homanhquan.productservice.dto.logout.response.LogoutResponse;
import org.homanhquan.productservice.service.LogoutService;
import org.homanhquan.productservice.service.TokenBlacklistService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LogoutServiceImpl implements LogoutService {

    private final TokenBlacklistService tokenBlacklistService;

    @Override
    public LogoutResponse logout(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            tokenBlacklistService.blacklistToken(token);
        }

        return LogoutResponse.builder()
                .message("Logged out successfully.")
                .timestamp(LocalDateTime.now())
                .build();
    }
}
