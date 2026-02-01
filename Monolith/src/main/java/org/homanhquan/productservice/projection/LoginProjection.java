package org.homanhquan.productservice.projection;

import org.homanhquan.productservice.enums.Role;

import java.util.UUID;

public interface LoginProjection {
    UUID getId();
    UUID getCartId();
    String getUsername();
    String getPassword();
    Role getRole();
    String getToken();
    String getFullName();
    String getEmail();
}
