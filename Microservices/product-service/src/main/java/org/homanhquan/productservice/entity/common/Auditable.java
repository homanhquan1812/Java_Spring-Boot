package org.homanhquan.productservice.entity.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Annotation/Method definition:
 * - @MappedSuperclass: Shares mappings (fields) with subclasses but does not create its own table.
 * - @Version: JPA auto-increments version, checks it on commit, throws OptimisticLockException on conflict.
 *
 * Why are both @UpdateTimestamp and @CreationTimestamp NOT RECOMMENDED:
 * - Hibernate-specific (not JPA or Spring Data standard).
 * - Values are set at flush time (timestamps may be slightly delayed).
 * - Can conflict with DB defaults (e.g. DEFAULT CURRENT_TIMESTAMP).
 * - Harder to test.
 */
@MappedSuperclass
@Getter
@Setter
public abstract class Auditable {

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
