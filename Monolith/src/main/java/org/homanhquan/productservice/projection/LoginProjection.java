package org.homanhquan.productservice.projection;

import org.homanhquan.productservice.enums.Role;

import java.util.UUID;

public interface LoginProjection {
    UUID getId();
    String getUsername();
    String getPassword();
    Role getRole();
    String getToken();
    String getFullName();
    String getEmail();
}
