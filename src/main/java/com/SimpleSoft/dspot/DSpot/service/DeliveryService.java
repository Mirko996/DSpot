package com.SimpleSoft.dspot.DSpot.service;

import com.SimpleSoft.dspot.DSpot.dto.delivery.ConfirmDeliveryRequest;
import com.SimpleSoft.dspot.DSpot.dto.delivery.DeliveryResponse;
import com.SimpleSoft.dspot.DSpot.dto.delivery.FailDeliveryRequest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

public interface DeliveryService {
    Page<DeliveryResponse> getDeliveriesForDriver(int page, int size);

    @Transactional
    void confirmDelivery(Long deliveryId, ConfirmDeliveryRequest request);

    @Transactional
    void failDelivery(Long deliveryId, FailDeliveryRequest request);
}
