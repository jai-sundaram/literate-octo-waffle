package com.meridian.commerce.catalog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    List<Product> findBySkuIn(Collection<String> skus);

    @Query("select p from Product p where p.category.slug = :slug")
    Page<Product> findByCategorySlug(@Param("slug") String slug, Pageable pageable);

    @Query("select p from Product p where p.active = true and " +
           "(lower(p.name) like lower(concat('%', :q, '%')) or lower(p.sku) = lower(:q))")
    Page<Product> search(@Param("q") String q, Pageable pageable);
}
