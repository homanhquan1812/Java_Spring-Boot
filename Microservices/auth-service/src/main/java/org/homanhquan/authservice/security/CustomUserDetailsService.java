package org.homanhquan.authservice.security;

import lombok.RequiredArgsConstructor;
import org.homanhquan.authservice.projection.LoginProjection;
import org.homanhquan.authservice.repository.LoginRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final LoginRepository loginRepository;
    // private final CustomUserDetails customUserDetails;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LoginProjection loginProjection = loginRepository.findByUsernameAndRole(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return CustomUserDetails.builder()
                .id(loginProjection.getId())
                .username(loginProjection.getUsername())
                .password(loginProjection.getPassword())
                .fullName(loginProjection.getFullName())
                .role(loginProjection.getRole())
                .build();
    }
}