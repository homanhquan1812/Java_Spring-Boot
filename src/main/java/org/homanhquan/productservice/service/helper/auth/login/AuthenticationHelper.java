package org.homanhquan.productservice.service.helper.auth.login;

import lombok.RequiredArgsConstructor;
import org.homanhquan.productservice.dto.login.request.LoginRequest;
import org.homanhquan.productservice.exception.UnauthorizedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationHelper {

    private final AuthenticationManager authenticationManager;

    public void authenticate(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.username(),
                            loginRequest.password()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Invalid username or password");
        }
    }
}
