package org.homanhquan.authservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalTime;

/**
 * NOT RECOMMENDED:
 * @Data:
 * - Includes @Getter, @Setter, @ToString, @EqualsAndHashCode, and @RequiredArgsConstructor.
 * - @ToString may trigger lazy loading (N + 1 problems) and leak sensitive data in logs (id, password, token, etc).
 *   Use @ToString(exclude = {"relationship", "password"}) if needed.
 * - @EqualsAndHashCode may cause unexpected behavior with JPA proxies and relationships.
 * - @RequiredArgsConstructor conflicts with @NoArgsConstructor.
 *
 * @Builder:
 * - Requires @AllArgsConstructor to work -> Conflicts with @NoArgsConstructor.
 * - Not suitable for entities that are frequently updated.
 *
 * @Index:
 *  - Index definitions should be managed at the database or migration level (Flyway/Liquibase), not in entities.
 */
@Entity
@Table(name = "brand") // Table's name
@Getter // Generates getter methods for all fields
@Setter // Generates setter methods for all non-final fields (PUBLIC by default)
@NoArgsConstructor // A constructor with zero parameters. Required by JPA/Hibernate for entity instantiation via reflection
@AllArgsConstructor
@Builder
public class Brand {

    /**
     * Entity shouldn't have validations such as @NotBlank, @NotNull, @NotEmpty, they are mostly for DTO.
     * Better only keep database constraints here.
     *
     * In @Column:
     * - name = address -> Schema's field name
     * - updatable = false -> Value can't be changed
     * - nullable = false -> Value can't be null
     * - unique = true -> Value must be unique
     * - length = 10 -> Maximum text length of value (Only for String) -> NOT COMMON compared to @Size
     * - columnDefinition = "TEXT" -> Column data type in database: TEXT (Mostly no need to use it)
     * - precision = 10 -> Total number of digits stored (BigDecimal)
     * - scale = 2 -> The number of digits after the decimal point (BigDecimal)
     *
     * In @DecimalMin: value = "0.0", inclusive = false
     * - If inclusive = false, numbers like 0.00, 0.0000 or less than 0 are not allowed.
     * - If inclusive = true, numbers like 0.00, 0.0000 are allowed, still less than 0 are not allowed.
     *
     * In @Size (Used in String, Collection, Map, Array):
     * - min = 5 -> Minimum text length of value.
     * - max = 10 -> Maximum text length of value.
     *
     * When to use @NotNull, @NotEmpty, @NotBlank:
     * - @NotNull: Use for non-String values (Integer, Long, LocalDate, enums).
     * - @NotEmpty: Use for collections or arrays (List, Set, Map).
     * - @NotBlank: Use for String fields.
     *
     * Which type of ID to choose:
     * - IDENTITY (Long/Integer):
     *   (+): Simple, easy to use, and widely supported across databases (MySQL, PostgreSQL, MSSQL).
     *   (-): Poor performance with batch inserts & Not suitable for distributed systems.
     *   (?): When to use - Small to medium apps, single database.
     *   @GeneratedValue(strategy = GenerationType.IDENTITY)
     *
     * - UUID v4 (UUID):
     *   (+): Globally unique, good for distributed systems.
     *   (-): Large storage size (16 bytes), poor index performance (random ordering).
     *   (?): When to use - Distributed systems, microservices.
     *   @GeneratedValue(strategy = GenerationType.UUID)
     *
     * - UUID v7 (UUID):
     *   (+): Globally unique + time-ordered (better index performance than UUID v4).
     *   (-): Large storage size (16 bytes).
     *   (?): When to use - Distributed systems with need for time-based sorting.
     *   @GeneratedValue(generator = "uuid7")
     *   @GenericGenerator(name = "uuid7", strategy = "org.hibernate.id.uuid.UuidVersion7Strategy")
     *
     * - ULID (String):
     *   (+): Time-ordered, URL-safe, case-insensitive, shorter than UUID when string-encoded.
     *   (-): Less common, requires external library.
     *   (?) When to use - When you need sortable IDs in string format.
     *   @PrePersist
     *   void generateId() {
     *        this.id = UlidCreator.getUlid().toString();
     *   }
     *
     * - SEQUENCE (Long):
     *   (+): Good performance with batch inserts (pre-allocates IDs).
     *   (-): Not portable across databases (MySQL > 8.0, MSSQL > 12, etc).
     *   (?): When to use - Internal IDs, business-defined numbering.
     *   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
     *   @SequenceGenerator(
     *           name = "product_seq",
     *           sequenceName = "product_sequence",
     *           initialValue = 10000001,  // Start from here
     *           allocationSize = 1        // Increase by 1
     *   )
     */
    @Id // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    @Setter(AccessLevel.NONE) // Prevent this field from adjusting outside
    private Long id;

    @Size(min = 1, max = 50, message = "Name must be between 1 and 50 characters")
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "opening_hours", nullable = false)
    private LocalTime openingHours;

    @Column(name = "closing_hours", nullable = false)
    private LocalTime closingHours;

    // Manual equals() & hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Brand other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * Relationship -> USE IT WITH CAUTIONS because:
     * - Collection relationships easily cause N+1 and performance issues.
     * - LAZY without fetch join easily leads to N+1 queries.
     * - Cascading with soft delete is error-prone.
     *
     * Soft-delete (CascadeType.PERSIST: Create, CascadeType.MERGE: Update)
     * Hard-delete: cascade = CascadeType.ALL, orphanRemoval = true
     *
     * By default:
     * - @OneToOne & @ManyToOne: FetchType.EAGER -> Loads all related entities immediately, even not necessary -> Better set LAZY
     * - @OneToMany & @ManyToMany: FetchType.LAZY
     *
     * N+1 problems occur when an application executes 1 query to fetch a list of entities and then N additional queries to fetch related data for each entity.
     * This leads to N+1 total queries instead of 1 optimized join query, causing performance issues.
     */
}