package org.homanhquan.authservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.homanhquan.authservice.dto.logout.response.LogoutResponse;
import org.homanhquan.authservice.service.LogoutService;
import org.homanhquan.authservice.service.TokenBlacklistService;
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

        return new LogoutResponse(
                "Logged out successfully",
                LocalDateTime.now()
        );
    }
}
