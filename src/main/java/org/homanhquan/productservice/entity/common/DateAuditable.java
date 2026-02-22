package org.homanhquan.productservice.entity.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Annotation/Method definition:
 * - @MappedSuperclass: Shares mappings (fields) with subclasses but does not create its own table.
 * - @Version: JPA auto-increments version, checks it on commit, throws OptimisticLockException on conflict.
 * - @Getter: Generates getter methods for all fields.
 * - @Setter: Generates setter methods for all non-final fields (PUBLIC by default).
 * - @PrePersist: Executes before entity is inserted (first save).
 * - @PreUpdate: Executes before entity is updated (subsequent saves).
 * ==================================================
 * Why are both @UpdateTimestamp and @CreationTimestamp NOT RECOMMENDED:
 * - Hibernate-specific (not JPA or Spring Data standard).
 * - Values are set at flush time (timestamps may be slightly delayed).
 * - Can conflict with DB defaults (e.g. DEFAULT CURRENT_TIMESTAMP).
 * - Harder to test.
 */
@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class DateAuditable {

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;
}
