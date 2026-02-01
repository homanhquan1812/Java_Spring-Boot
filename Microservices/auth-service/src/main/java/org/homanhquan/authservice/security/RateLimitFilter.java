package org.homanhquan.authservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.authservice.dto.error.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    // Cache với giới hạn kích thước
    private final Map<String, BucketEntry> cache = new ConcurrentHashMap<>();

    private static final int CAPACITY = 5;
    private static final Duration REFILL_DURATION = Duration.ofMinutes(1);
    private static final int MAX_CACHE_SIZE = 10000; // Giới hạn số IP lưu
    private static final Duration CACHE_ENTRY_TTL = Duration.ofMinutes(10); // TTL cho mỗi entry

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // Chỉ apply rate limit cho /auth/login
        if (!requestURI.equals("/auth/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = getClientIP(request);
        Bucket bucket = resolveBucket(ip);

        if (bucket.tryConsume(1)) {
            log.debug("Request allowed for IP: {}", ip);
            filterChain.doFilter(request, response);
        } else {
            handleRateLimitExceeded(request, response, ip);
        }
    }

    /**
     * Xử lý khi rate limit bị vượt quá
     */
    private void handleRateLimitExceeded(HttpServletRequest request,
                                         HttpServletResponse response,
                                         String ip) throws IOException {
        log.warn("Rate limit exceeded for IP: {} on URI: {}", ip, request.getRequestURI());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .success(false)
                .message("Too many login attempts. Please try again later.")
                .status(HttpStatus.TOO_MANY_REQUESTS.value())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Retry-After", "60"); // Standard header

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }

    /**
     * Lấy hoặc tạo bucket cho IP
     */
    private Bucket resolveBucket(String ip) {
        cleanupExpiredEntries(); // Cleanup trước khi thêm mới

        BucketEntry entry = cache.compute(ip, (k, v) -> {
            if (v == null || v.isExpired()) {
                return new BucketEntry(createNewBucket());
            }
            v.updateLastAccess();
            return v;
        });

        return entry.getBucket();
    }

    /**
     * Tạo bucket mới với rate limit config
     */
    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(CAPACITY)
                .refillIntervally(CAPACITY, REFILL_DURATION)
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Cleanup các entries đã hết hạn
     */
    private void cleanupExpiredEntries() {
        if (cache.size() > MAX_CACHE_SIZE) {
            cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
            log.info("Cleaned up expired rate limit entries. Current size: {}", cache.size());
        }
    }

    /**
     * Lấy IP thực của client (xử lý proxy/load balancer)
     */
    private String getClientIP(HttpServletRequest request) {
        // Kiểm tra X-Forwarded-For (nginx, load balancer)
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isEmpty()) {
            return xfHeader.split(",")[0].trim();
        }

        // Kiểm tra X-Real-IP (nginx)
        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }

        // Fallback to remote address
        return request.getRemoteAddr();
    }

    /**
     * Wrapper class để lưu bucket + timestamp
     */
    private static class BucketEntry {
        private final Bucket bucket;
        private volatile long lastAccessTime;

        public BucketEntry(Bucket bucket) {
            this.bucket = bucket;
            this.lastAccessTime = System.currentTimeMillis();
        }

        public Bucket getBucket() {
            return bucket;
        }

        public void updateLastAccess() {
            this.lastAccessTime = System.currentTimeMillis();
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - lastAccessTime > CACHE_ENTRY_TTL.toMillis();
        }
    }
}
