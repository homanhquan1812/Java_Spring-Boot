package org.homanhquan.productservice.projection;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UserProjection {
    UUID getId();
    String getStatus();
    String getRole();
    String getBrandName();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
    LocalDateTime getDeletedAt();
    String getFullName();
    String getUsername();
    String getEmail();
    String getPhone();
    String getGender();
    String getAddress();
}
