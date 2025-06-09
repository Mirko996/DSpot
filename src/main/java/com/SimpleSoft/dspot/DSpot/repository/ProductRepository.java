package com.SimpleSoft.dspot.DSpot.repository;

import com.SimpleSoft.dspot.DSpot.domain.Distributor;
import com.SimpleSoft.dspot.DSpot.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsBySkuAndDistributor(String sku, Distributor distributor);

    Page<Product> findAllByDistributorAndIsHiddenFalse(Distributor distributor, Pageable pageable);

    Page<Product> findAllByDistributor(Distributor distributor, Pageable pageable);

    Product findByIdAndDistributor(Long id, Distributor distributor);
}
