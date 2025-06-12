package com.SimpleSoft.dspot.DSpot.controller;

import com.SimpleSoft.dspot.DSpot.dto.delivery.ConfirmDeliveryRequest;
import com.SimpleSoft.dspot.DSpot.dto.delivery.DeliveryResponse;
import com.SimpleSoft.dspot.DSpot.dto.delivery.FailDeliveryRequest;
import com.SimpleSoft.dspot.DSpot.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping
    public ResponseEntity<Page<DeliveryResponse>> getDeliveries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<DeliveryResponse> deliveries = deliveryService.getDeliveriesForDriver(page, size);
        return ResponseEntity.ok(deliveries);
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<Void> confirmDelivery(
            @PathVariable Long id,
            @RequestBody ConfirmDeliveryRequest request) {
        deliveryService.confirmDelivery(id, request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/fail")
    public ResponseEntity<Void> failDelivery(
            @PathVariable Long id,
            @RequestBody FailDeliveryRequest request) {
        deliveryService.failDelivery(id, request);
        return ResponseEntity.ok().build();
    }
}