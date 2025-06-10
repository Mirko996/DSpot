package com.SimpleSoft.dspot.DSpot.service;

import com.SimpleSoft.dspot.DSpot.dto.order.CreateOrderRequest;
import com.SimpleSoft.dspot.DSpot.dto.order.OrderResponse;
import com.SimpleSoft.dspot.DSpot.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

public interface OrderService {
    @Transactional
    OrderResponse createOrder(CreateOrderRequest request);

    Page<OrderResponse> getOrdersForCurrentUser(int page, int size);

    OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus);
}
