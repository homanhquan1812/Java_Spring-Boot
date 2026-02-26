package org.homanhquan.productservice.security.rateLimitFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.productservice.exception.helper.global.error.response.ErrorResponseHelper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitExceededHandler {

    private final RateLimitBucketService bucketService;
    private final ErrorResponseHelper errorResponseHelper;

    public void handleExceeded(HttpServletRequest request, HttpServletResponse response, String ip) throws IOException {
        log.warn("Rate limit exceeded for IP: {} on URI: {}", ip, request.getRequestURI());
        response.setHeader("Retry-After", String.valueOf(bucketService.getRefillDurationSeconds()));
        errorResponseHelper.sendErrorResponse(
                request, response,
                HttpStatus.TOO_MANY_REQUESTS,
                "Too many login attempts. Please try again later."
        );
    }
}
