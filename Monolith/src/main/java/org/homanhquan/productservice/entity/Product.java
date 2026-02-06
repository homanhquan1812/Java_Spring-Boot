package org.homanhquan.productservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.homanhquan.productservice.entity.common.Auditable;
import org.homanhquan.productservice.enums.Status;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Hibernate scans Entity classes and maps them to database tables/columns.
 * ==================================================
 * Annotation explanation:
 * - @Entity: Marks a Java class as a persistent entity, meaning it is mapped to a database table and managed by the ORM framework.
 * - @Table(name = "???"): Table's name.
 * - @Getter: Generates getter methods for all fields.
 * - @Setter: Generates setter methods for all non-final fields (PUBLIC by default).
 * - @NoArgsConstructor: A constructor with zero parameters. Required by JPA/Hibernate for entity instantiation via reflection.
 * - @Column: Maps a class field to a database table column and to configure column details.
 *   + name = address -> Schema's field name.
 *   + updatable = false -> Value can't be changed.
 *   + nullable = false -> Value can't be null.
 *   + unique = true -> Value must be unique.
 *   + length = 10 -> Maximum text length of value (Only for String) -> NOT COMMON compared to @Size.
 *   + columnDefinition = "TEXT" -> Column data type in database: TEXT (Mostly no need to use it).
 *   + precision = 10 -> Total number of digits stored (BigDecimal).
 *   + scale = 2 -> The number of digits after the decimal point (BigDecimal).
 * ==================================================
 * Annotations NOT RECOMMENDED in Entity:
 * - @Data: Includes @Getter, @Setter, @ToString, @EqualsAndHashCode, and @RequiredArgsConstructor.
 *   + @ToString may trigger lazy loading (N + 1 problems) and leak sensitive data in logs (id, password, token, etc).
 *     Use @ToString(exclude = {"relationship", "password"}) if needed.
 *   + @EqualsAndHashCode may cause unexpected behavior with JPA proxies and relationships.
 *   + @RequiredArgsConstructor conflicts with @NoArgsConstructor.
 * - @Builder: An annotation that implements the Builder design pattern, allowing you to create objects step by step with a fluent, readable API instead of using long constructors.
 *   + Requires @AllArgsConstructor to work -> Conflicts with @NoArgsConstructor.
 *   + @Builder encourages immutable-style object creation, which conflicts with JPA entity lifecycle & change tracking.
 *   + Better suited for DTOs, not persisted entities.
 * - @Index: Defines a database index on one or more table columns to improve query performance, especially for search and filtering operations.
 *   + Index definitions should be managed at the database or migration level (Flyway/Liquibase), not in entities.
 * - @NotBlank, @NotNull, @NotEmpty: Validation annotations.
 *   + Intended for input validation (DTO layer).
 *   + Entity should focus on database constraints and domain invariants (e.g. email != null).
 */
@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
public class Product extends Auditable {

    /**
     * Which type of ID to choose?
     * - IDENTITY (Long/Integer): Auto-increment ID.
     *   (+): Simple, easy to use, and widely supported across databases (MySQL, PostgreSQL, MSSQL).
     *   (-): Poor performance with batch inserts & Not suitable for distributed systems.
     *   (?): When to use - Small to medium apps, single database.
     * - UUID (UUID): Universally Unique Identifier that is used to uniquely identify information across systems, with an extremely low chance of duplication.
     *   (+): Globally unique.
     *   (-): Large storage size (16 bytes).
     *   (?): When to use - Distributed system, microservices.
     *   v1 (Not common): Time-ordered ID & Security risks (e.g. MAC address exposed).
     *   v4 (By default): Random ID -> No sorting -> Poor index performance.
     *   v7 (Not common): Time-ordered ID based on timestamps -> Good sorting -> Better index performance.
     * - SEQUENCE (Long): Generates ordered, incremental numbers, commonly used for auto‑generating primary keys in a safe and scalable way.
     *   (+): Good performance with batch inserts (pre-allocates IDs).
     *   (-): Not portable across databases (MySQL > 8.0, MSSQL > 12, etc).
     *   (?): When to use - Internal IDs, business-defined numbering.
     *   SKU (Stock Keeping Unit) vs Invoice Number (Transaction SEQUENCE)
     *   -> "PHONE-IP15-BLK-128GB": Category-Brand-Color-Storage (Product identifier - describes product attributes & Static, no sequence)
     *   -> "INV-2024-00001": Prefix-Year-Sequence (Transaction identifier - sequential numbering & Dynamic, always incremental).
     * ==================================================
     * Annotation explanation:
     * - @Id: Primary key.
     * - @GeneratedValue: Specifies that the value of the primary key will be automatically generated by the persistence provider (e.g., Hibernate).
     * - @Setter(AccessLevel.NONE): Prevents this field from adjusting outside.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    @Setter(AccessLevel.NONE)
    private Long id;

    /**
     * Annotation explanation:
     * - @Size: Validate the size or length of a value, such as strings, collections, maps, or arrays, by specifying minimum and/or maximum limits:
     *   + min = 5 -> Minimum text length of value.
     *   + max = 10 -> Maximum text length of value.
     */
    @Size(min = 1, max = 50, message = "Name must be between 1 and 50 characters")
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    /**
     * Annotation explanation:
     * - @Enumerated(EnumType.STRING): Enums with String type.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    /**
     * Annotation explanation:
     * - @DecimalMin(value = "0.0", inclusive = false): Validate that a numeric value is greater than or equal to a specified minimum decimal value:
     *   + If inclusive = false, numbers like 0.00, 0.0000 or less than 0 are not allowed.
     *   + If inclusive = true, numbers like 0.00, 0.0000 are allowed, still less than 0 are not allowed.
     * - @Digits(integer = 16, fraction = 2): Validates that a number has at most 16 digits before the decimal point
     *   and at most 2 digits after the decimal point (e.g. 9999999999999999.99 (OK), 100.5 (NO), 100.999 (NO)).
     */
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 16, fraction = 2, message = "Price format is invalid")
    @Column(name = "price", nullable = false, precision = 18, scale = 2)
    private BigDecimal price;

    @Column(name = "created_by", updatable = false)
    private UUID createdBy;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "deleted_by")
    private UUID deletedBy;

    // Manual equals() & hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product other)) return false;
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
     * There are 2 deletion types:
     * - Soft-delete (CascadeType.PERSIST: Create, CascadeType.MERGE: Update).
     * - Hard-delete: cascade = CascadeType.ALL, orphanRemoval = true.
     * By default:
     * - @OneToOne & @ManyToOne: FetchType.EAGER -> Loads all related entities immediately, even not necessary -> Better set LAZY.
     * - @OneToMany & @ManyToMany: FetchType.LAZY.
     * ==================================================
     * N+1 problems occur when an application executes 1 query to fetch a list of entities and then N additional queries to fetch related data for each entity.
     * This leads to N+1 total queries instead of 1 optimized join query, causing performance issues.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;
}
