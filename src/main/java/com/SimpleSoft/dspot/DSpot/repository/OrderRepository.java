package com.SimpleSoft.dspot.DSpot.repository;

import com.SimpleSoft.dspot.DSpot.domain.Distributor;
import com.SimpleSoft.dspot.DSpot.domain.Order;
import com.SimpleSoft.dspot.DSpot.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findAllByDistributor(Distributor distributor, Pageable pageable);

    Page<Order> findAllByStoreUserAndDistributor(User storeUser, Distributor distributor, Pageable pageable);

    Page<Order> findAllBySalesRepAndDistributor(User salesRep, Distributor distributor, Pageable pageable);

    Order findByIdAndDistributor(Long id, Distributor distributor);
}
