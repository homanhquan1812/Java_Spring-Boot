package org.homanhquan.productservice.exception.helper.global.error.response;

import jakarta.servlet.http.HttpServletRequest;
import org.homanhquan.productservice.dto.error.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class ErrorResponseHelper {

    public ResponseEntity<ErrorResponse> buildErrorResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            Map<String, String> errors
    ) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .success(false)
                .message(message)
                .status(status.value())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .errors(errors)
                .build();

        return ResponseEntity.status(status).body(errorResponse);
    }
}
