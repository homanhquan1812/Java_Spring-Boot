package org.homanhquan.productservice.security.userDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.homanhquan.productservice.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private UUID id;
    private String username;
    private String password;
    private String fullName;
    private Role role;

    /**
     * If you have ROLE (e.g. ADMIN, USER), add prefix "ROLE_".
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }
}