package org.homanhquan.productservice.repository;

import org.homanhquan.productservice.entity.Product;
import org.homanhquan.productservice.projection.ProductProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Annotation/Method definition:
 * - @Repository: A bean for the data access layer (DAO). Spring adds automatic exception translation,
 *   converting JDBC/SQL exceptions into Spring’s DataAccessException (a runtime exception to handle database errors).
 * - @Query("""..."""): Defines a custom JPQL or SQL (using nativeQuery) query directly on a repository method.
 *   + JPQL (Java Persistence Query Language): Writes database queries based on Java entities instead of table names, making queries portable across databases and less error-prone.
 *     It naturally supports relationships, lazy/eager loading, caching, and can auto-generate optimized count queries for pagination.
 *   + SQL (using nativeQuery): Uses when complex database-specific features
 *
 * When to use nativeQuery?
 * - Complex database-specific features (window functions, CTEs, JSON operations).
 * - Performance-critical queries requiring specific SQL optimization.
 *
 * Some SQL clauses:
 * - CREATE/DROP DATABASE <dbname>: Create/Drop a database.
 * - CREATE TABLE <tbname>(...): Create a table.
 * - DROP TABLE <tbname>: Drop a table.
 * - INSERT INTO <tbname> VALUES (...): Insert data into table.
 * - SELECT * FROM <tbname>: Get all rows from table.
 * - SELECT <column1>, <column2> FROM <tbname>: Only choose 2 columns from table.
 * - SELECT * FROM <tbname> LIMIT 5 OFFSET 5: Only get 5 rows in total and skip 5 first rows.
 * - SELECT * FROM <tbname> WHERE <condition such as age >= 25 or <column1> = "???"> ORDER BY <column1> DESC:
 *   Get all rows that meet requirements, ordered descendingly based on <column1>.
 * - ALTER TABLE <tbname> ADD <column1> <type> <constraint>: Add <column1> in <tbname>.
 * - ALTER TABLE <tbname> ALTER <column1> <type> <constraint>: Change constraints in <tbname>.
 * - ALTER TABLE <tbname> RENAME COLUMN <column1> TO <column2>: Change column's name.
 * - UPDATE <tbname> SET <column1> = ??? WHERE <column?> = ???: Update value in table.
 * - DELETE FROM <tbname> WHERE <column?> BETWEEN a AND b: Delete rows (such as ID) from a and b.
 * - TRUNCATE TABLE <tbname>: Delete all rows but still keep the table.
 * - SELECT * FROM <tbname1> t1 LEFT JOIN <tbname2> t2 ON t1.<column1> = t2.<column2> WHERE <condition such as age >= 25 or <column1> = "???">:
 *   Left join for 2 tables with specific conditions.
 * - SELECT <column1>, COUNT(*) FROM <tbname> GROUP BY <column1> HAVING COUNT(*) > 1:
 *   Finds values of <column1> that appear more than once (duplicate values), and counts how many rows each value has.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("""
        SELECT 
            p.id as id,
            p.name as name,
            p.description as description,
            p.price as price,
            p.status as status,
            p.createdAt as createdAt,
            p.updatedAt as updatedAt,
            p.deletedAt as deletedAt,
            p.createdBy as createdBy,
            p.updatedBy as updatedBy,
            p.deletedBy as deletedBy, 
            p.version as version,
            b.name as brandName
        FROM Product p
        LEFT JOIN p.brand b
        WHERE p.deletedAt IS NULL
        """)
    List<ProductProjection> findAllProductsWithBrandName();

    @Query("""
        SELECT 
            p.id as id,
            p.name as name,
            p.description as description,
            p.price as price,
            p.status as status,
            p.createdAt as createdAt,
            p.updatedAt as updatedAt,
            p.deletedAt as deletedAt,
            p.createdBy as createdBy,
            p.updatedBy as updatedBy,
            p.deletedBy as deletedBy, 
            p.version as version,
            b.name as brandName
        FROM Product p
        LEFT JOIN p.brand b
        WHERE p.deletedAt IS NULL
        """)
    Page<ProductProjection> findProductsPageWithBrandName(Pageable pageable);

    @Query("""
        SELECT 
            p.id as id,
            p.name as name,
            p.description as description,
            p.price as price,
            p.status as status,
            p.createdAt as createdAt,
            p.updatedAt as updatedAt,
            p.deletedAt as deletedAt,
            p.createdBy as createdBy,
            p.updatedBy as updatedBy,
            p.deletedBy as deletedBy, 
            p.version as version,
            b.name as brandName
        FROM Product p
        LEFT JOIN p.brand b
        WHERE p.id = :id AND p.deletedAt IS NULL
        """)
    Optional<ProductProjection> findProductByIdWithBrandName(@Param("id") Long productId);

    /**
     * JOIN FETCH to solve N+1 problem:
     * @Query("""SELECT p FROM Product p LEFT JOIN FETCH p.brand WHERE p.deletedAt IS NULL""")
     *
     * EntityGraph instead of JOIN FETCH:
     * @EntityGraph(attributePaths = {"brand"})
     * @Query("""SELECT p FROM Product p WHERE p.deletedAt IS NULL""")
     */
}
