package com.SimpleSoft.dspot.DSpot.repository;

import com.SimpleSoft.dspot.DSpot.domain.Distributor;
import com.SimpleSoft.dspot.DSpot.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsBySkuAndDistributor(String sku, Distributor distributor);

    Page<Product> findAllByDistributorAndIsHiddenFalse(Distributor distributor, Pageable pageable);

    Page<Product> findAllByDistributor(Distributor distributor, Pageable pageable);

    Product findByIdAndDistributor(Long id, Distributor distributor);

    @Query("""
    SELECT p FROM Product p
    WHERE p.distributor = :distributor
      AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :search, '%')))
      AND (:inStock IS NULL OR p.stockQuantity > 0)
      AND (:includeHidden = TRUE OR p.isHidden = FALSE)""")
    Page<Product> searchProducts(
            @Param("distributor") Distributor distributor,
            @Param("search") String search,
            @Param("inStock") Boolean inStock,
            @Param("includeHidden") boolean includeHidden,
            Pageable pageable
    );
}
