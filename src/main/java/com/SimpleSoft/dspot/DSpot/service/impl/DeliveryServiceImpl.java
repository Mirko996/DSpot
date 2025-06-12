package com.SimpleSoft.dspot.DSpot.service.impl;

import com.SimpleSoft.dspot.DSpot.domain.Delivery;
import com.SimpleSoft.dspot.DSpot.domain.Distributor;
import com.SimpleSoft.dspot.DSpot.domain.Order;
import com.SimpleSoft.dspot.DSpot.domain.User;
import com.SimpleSoft.dspot.DSpot.dto.delivery.ConfirmDeliveryRequest;
import com.SimpleSoft.dspot.DSpot.dto.delivery.DeliveryResponse;
import com.SimpleSoft.dspot.DSpot.dto.delivery.FailDeliveryRequest;
import com.SimpleSoft.dspot.DSpot.enums.DeliveryStatus;
import com.SimpleSoft.dspot.DSpot.exception.ServiceException;
import com.SimpleSoft.dspot.DSpot.repository.DeliveryRepository;
import com.SimpleSoft.dspot.DSpot.repository.DistributorRepository;
import com.SimpleSoft.dspot.DSpot.repository.UserRepository;
import com.SimpleSoft.dspot.DSpot.security.AuthUtils;
import com.SimpleSoft.dspot.DSpot.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements com.SimpleSoft.dspot.DSpot.service.DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final DistributorRepository distributorRepository;

    @Override
    public Page<DeliveryResponse> getDeliveriesForDriver(int page, int size) {
        Long distributorId = AuthUtils.getCurrentDistributorId();
        String email = AuthUtils.getCurrentUserEmail();
        String role = AuthUtils.getCurrentUserRole();

        if (!"DRIVER".equalsIgnoreCase(role))
            throw new ServiceException("Access denied: only drivers can view assigned deliveries");

        User driver = userRepository.findByEmail(email)
                .orElseThrow(() -> new ServiceException("Driver not found"));

        Distributor distributor = distributorRepository.findById(distributorId)
                .orElseThrow(() -> new ServiceException("Distributor not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Delivery> deliveries = deliveryRepository.findAllByAssignedDriverAndOrder_Distributor(driver, distributor, pageable);

        return deliveries.map(this::toResponse);
    }

    @Transactional
    public void confirmDelivery(Long deliveryId, ConfirmDeliveryRequest request) {
        Delivery delivery = getAndValidateDeliveryForCurrentDriver(deliveryId);

        delivery.setStatus(DeliveryStatus.DELIVERED);
        delivery.setProof(request.getProof());

        Order order = delivery.getOrder();
        productService.finalizeDeliveryStock(order.getItems());

        deliveryRepository.save(delivery);
    }

    @Transactional
    public void failDelivery(Long deliveryId, FailDeliveryRequest request) {
        Delivery delivery = getAndValidateDeliveryForCurrentDriver(deliveryId);

        delivery.setStatus(DeliveryStatus.FAILED);
        delivery.setProof(request.getProof());

        Order order = delivery.getOrder();
        productService.releaseReservedStock(order.getItems());

        deliveryRepository.save(delivery);
    }

    private void updateDeliveryStatus(Long deliveryId, DeliveryStatus newStatus, String proof) {
        Long distributorId = AuthUtils.getCurrentDistributorId();
        String email = AuthUtils.getCurrentUserEmail();

        User driver = userRepository.findByEmail(email)
                .orElseThrow(() -> new ServiceException("Driver not found"));

        Distributor distributor = distributorRepository.findById(distributorId)
                .orElseThrow(() -> new ServiceException("Distributor not found"));

        Delivery delivery = deliveryRepository.findByIdAndOrder_Distributor(deliveryId, distributor);
        if (delivery == null || !driver.equals(delivery.getAssignedDriver())) {
            throw new ServiceException("Unauthorized or invalid delivery access");
        }

        delivery.setStatus(newStatus);
        delivery.setProof(proof);
        deliveryRepository.save(delivery);
    }

    private Delivery getAndValidateDeliveryForCurrentDriver(Long deliveryId) {
        Long distributorId = AuthUtils.getCurrentDistributorId();
        String email = AuthUtils.getCurrentUserEmail();
        String role = AuthUtils.getCurrentUserRole();

        if (!"DRIVER".equalsIgnoreCase(role)) {
            throw new ServiceException("Only drivers can update deliveries");
        }

        User driver = userRepository.findByEmail(email)
                .orElseThrow(() -> new ServiceException("Driver not found"));

        Distributor distributor = distributorRepository.findById(distributorId)
                .orElseThrow(() -> new ServiceException("Distributor not found"));

        Delivery delivery = deliveryRepository.findByIdAndOrder_Distributor(deliveryId, distributor);
        if (delivery == null || !driver.equals(delivery.getAssignedDriver())) {
            throw new ServiceException("Unauthorized or invalid delivery access");
        }

        return delivery;
    }


    private DeliveryResponse toResponse(Delivery delivery) {
        return new DeliveryResponse(
                delivery.getId(),
                delivery.getOrder().getId(),
                delivery.getOrder().getStoreUser().getName(),
                delivery.getStatus(),
                delivery.getProof(),
                delivery.getCreatedAt(),
                delivery.getUpdatedAt()
        );
    }
}
