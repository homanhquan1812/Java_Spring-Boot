package org.homanhquan.productservice.repository;

import org.homanhquan.productservice.entity.Product;
import org.homanhquan.productservice.projection.ProductProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
}
