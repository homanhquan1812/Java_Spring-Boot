package org.homanhquan.productservice.service.helper.auth.login;

import lombok.RequiredArgsConstructor;
import org.homanhquan.productservice.dto.login.request.LoginRequest;
import org.homanhquan.productservice.exception.UnauthorizedException;
import org.homanhquan.productservice.security.userDetails.CustomUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationHelper {

    private final AuthenticationManager authenticationManager;

    public CustomUserDetails authenticate(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.username(),
                            loginRequest.password()
                    )
            );

            return (CustomUserDetails) authentication.getPrincipal();
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Invalid username or password");
        }
    }
}
