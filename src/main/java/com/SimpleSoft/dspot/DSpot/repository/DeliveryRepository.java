package com.SimpleSoft.dspot.DSpot.repository;

import com.SimpleSoft.dspot.DSpot.domain.Delivery;
import com.SimpleSoft.dspot.DSpot.domain.Distributor;
import com.SimpleSoft.dspot.DSpot.domain.User;
import com.SimpleSoft.dspot.DSpot.enums.DeliveryStatus;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    Page<Delivery> findAllByAssignedDriverAndOrder_Distributor(
            User driver,
            Distributor distributor,
            Pageable pageable
    );

    List<Delivery> findByOrder_IdAndOrder_Distributor(Long orderId, Distributor distributor);

    Delivery findByIdAndOrder_Distributor(Long id, Distributor distributor);

    List<Delivery> findByAssignedDriverAndStatus(User driver, DeliveryStatus status);
}
