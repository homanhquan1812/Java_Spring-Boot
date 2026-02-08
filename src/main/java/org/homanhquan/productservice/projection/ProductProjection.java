package org.homanhquan.productservice.projection;

import org.homanhquan.productservice.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Projections fetch only selected fields instead of full entities,
 * avoiding unnecessary loading of related entities.
 */
public interface ProductProjection {
    Long getId();
    String getName();
    String getDescription();
    BigDecimal getPrice();
    String getBrandName();
    Status getStatus();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
    LocalDateTime getDeletedAt();
    UUID getCreatedBy();
    UUID getUpdatedBy();
    UUID getDeletedBy();
    Long getVersion();
}
