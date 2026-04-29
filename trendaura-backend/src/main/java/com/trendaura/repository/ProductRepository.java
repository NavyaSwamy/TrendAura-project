package com.trendaura.repository;

import com.trendaura.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ===============================
    // BASIC LISTING
    // ===============================

    Page<Product> findByActiveTrue(Pageable pageable);

    Page<Product> findByActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    Page<Product> findByActiveTrueOrderByPriceDesc(Pageable pageable);

    // ===============================
    // SEARCH FILTER ENGINE
    // ===============================

    @Query("""
            SELECT p FROM Product p
            WHERE p.active = true
            AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%',:keyword,'%')))
            AND (:category IS NULL OR p.category = :category)
            AND (:stateCode IS NULL OR p.stateCode = :stateCode)
            AND (:fashionType IS NULL OR p.fashionType = :fashionType)
            """)
    Page<Product> advancedSearch(
            String keyword,
            String category,
            String stateCode,
            String fashionType,
            Pageable pageable
    );

}