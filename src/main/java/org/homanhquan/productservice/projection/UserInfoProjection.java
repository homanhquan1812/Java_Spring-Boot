package org.homanhquan.productservice.projection;

import org.homanhquan.productservice.enums.Role;

import java.util.UUID;

public interface UserInfoProjection {
    UUID getId();
    String getUsername();
    String getPassword();
    String getFullName();
    Role getRole();
}
