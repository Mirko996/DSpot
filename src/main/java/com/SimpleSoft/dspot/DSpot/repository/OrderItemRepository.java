package com.SimpleSoft.dspot.DSpot.repository;

import com.SimpleSoft.dspot.DSpot.domain.Order;
import com.SimpleSoft.dspot.DSpot.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder(Order order);
}
