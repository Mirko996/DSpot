package com.SimpleSoft.dspot.DSpot.repository;

import com.SimpleSoft.dspot.DSpot.domain.Distributor;
import com.SimpleSoft.dspot.DSpot.domain.Order;
import com.SimpleSoft.dspot.DSpot.domain.User;
import com.SimpleSoft.dspot.DSpot.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findAllByDistributor(Distributor distributor, Pageable pageable);

    Page<Order> findAllByStoreUserAndDistributor(User storeUser, Distributor distributor, Pageable pageable);

    Page<Order> findAllBySalesRepAndDistributor(User salesRep, Distributor distributor, Pageable pageable);

    Order findByIdAndDistributor(Long id, Distributor distributor);

    @Query("""
    SELECT o FROM Order o
    WHERE o.distributor = :distributor
      AND (:storeUser IS NULL OR o.storeUser = :storeUser)
      AND (:salesRep IS NULL OR o.salesRep = :salesRep)
      AND (:status IS NULL OR o.status = :status)""")
    Page<Order> searchOrders(
            @Param("distributor") Distributor distributor,
            @Param("storeUser") User storeUser,
            @Param("salesRep") User salesRep,
            @Param("status") OrderStatus status,
            Pageable pageable
    );

}
