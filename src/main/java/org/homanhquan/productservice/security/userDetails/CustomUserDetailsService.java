package org.homanhquan.productservice.security.userDetails;

import lombok.RequiredArgsConstructor;
import org.homanhquan.productservice.projection.UserInfoProjection;
import org.homanhquan.productservice.repository.UserInfoRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserInfoRepository userInfoRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfoProjection userInfoProjection = userInfoRepository.findByUsernameAndRole(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return CustomUserDetails.builder()
                .id(userInfoProjection.getId())
                .username(userInfoProjection.getUsername())
                .password(userInfoProjection.getPassword())
                .fullName(userInfoProjection.getFullName())
                .role(userInfoProjection.getRole())
                .build();
    }
}