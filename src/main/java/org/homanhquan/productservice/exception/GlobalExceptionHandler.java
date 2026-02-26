package org.homanhquan.productservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.productservice.dto.error.response.ErrorResponse;
import org.homanhquan.productservice.exception.helper.global.error.response.ErrorResponseHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Exception Handling can occur everywhere. If there's an exception, local handler (@ExceptionHandler) in Controller,
 * or global handler (@ControllerAdvice/@RestControllerAdvice) returns error response.
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorResponseHelper errorResponseHelper;

    /**
     * Handles validation errors from @Valid annotated requests (e.g. Invalid email, date).
     * Status code: 400.
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        log.warn("Validation failed");

        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });

        return errorResponseHelper.buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                request,
                validationErrors
        );
    }

    /**
     * Handles custom bad request exceptions for business logic (e.g. User below 18 years old can't use this feature).
     * Status code: 400.
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            BadRequestException ex,
            HttpServletRequest request
    ) {
        log.warn("Bad request: {}", ex.getMessage());

        return errorResponseHelper.buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request,
                null
        );
    }

    /**
     * Handles authentication failures (e.g. Incorrect username or password).
     * Status code: 401.
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler({UnauthorizedException.class, BadCredentialsException.class})
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            Exception ex,
            HttpServletRequest request
    ) {
        log.warn("Unauthorized: {}", ex.getMessage());

        return errorResponseHelper.buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Invalid username or password",
                request,
                null
        );
    }

    /**
     * Handles authorization failures (e.g. Users can't access admin panel).
     * Status code: 403.
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {
        log.warn("Access denied: {}", ex.getMessage());

        return errorResponseHelper.buildErrorResponse(
                HttpStatus.FORBIDDEN,
                "Access denied",
                request,
                null
        );
    }

    /**
     * Handles resource not found exceptions.
     * Status code: 404.
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {

        log.warn("Resource not found: {}", ex.getMessage());

        return errorResponseHelper.buildErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request,
                null
        );
    }

    /**
     * Handles all uncaught exceptions (Internal server error).
     * Status code: 500.
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Unexpected error", ex);

        return errorResponseHelper.buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                request,
                null
        );
    }
}