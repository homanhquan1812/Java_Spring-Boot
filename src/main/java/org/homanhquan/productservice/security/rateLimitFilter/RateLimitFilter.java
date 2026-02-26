package org.homanhquan.productservice.security.rateLimitFilter;

import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
@Getter
@Setter
public class RateLimitFilter extends OncePerRequestFilter {

    private final ClientIPResolver clientIPResolver;
    private final RateLimitExceededHandler rateLimitExceededHandler;
    private final RateLimitBucketService rateLimitBucketService;

    /**
     * Flow:
     * 1. Get request URI (just path) & skip non-login requests.
     * 2. Resolve bucket by client IP.
     *    Bucket holds tokens, and each IP has its own bucket holding a maximum of 5 tokens -> You can only try 4 times.
     *    Each incoming request consumes 1 token.
     * 3. Allow request if tokens available, reject with 429 otherwise.
     *     currentTokens = 5  // Initial
     *     currentTokens = 4  // After request 1
     *     currentTokens = 3  // After request 2
     *     currentTokens = 2  // After request 3
     *     currentTokens = 1  // After request 4
     *     currentTokens = 0  // Empty -> 429 error
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        if (!requestURI.startsWith("/auth/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = clientIPResolver.getClientIP(request);
        Bucket bucket = rateLimitBucketService.resolveBucket(ip);

        if (bucket.tryConsume(1)) {
            log.debug("Request allowed for IP: {}", ip);
            filterChain.doFilter(request, response);
        } else {
            rateLimitExceededHandler.handleExceeded(request, response, ip);
        }
    }
}
