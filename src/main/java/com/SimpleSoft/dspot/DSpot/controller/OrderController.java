package com.SimpleSoft.dspot.DSpot.controller;


import com.SimpleSoft.dspot.DSpot.dto.order.CreateOrderRequest;
import com.SimpleSoft.dspot.DSpot.dto.order.OrderResponse;
import com.SimpleSoft.dspot.DSpot.dto.order.OrderStatusUpdateRequest;
import com.SimpleSoft.dspot.DSpot.enums.OrderStatus;
import com.SimpleSoft.dspot.DSpot.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<OrderResponse> orders = orderService.getOrdersForCurrentUser(page, size);
        return ResponseEntity.ok(orders);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody OrderStatusUpdateRequest request) {
        OrderStatus newStatus = OrderStatus.valueOf(request.getStatus().toUpperCase());
        OrderResponse response = orderService.updateOrderStatus(id, newStatus);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getOrders(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        OrderStatus parsedStatus = null;
        if (status != null && !status.isBlank()) {
            parsedStatus = OrderStatus.valueOf(status.toUpperCase());
        }

        Page<OrderResponse> orders = orderService.getOrdersFiltered(parsedStatus, page, size);
        return ResponseEntity.ok(orders);
    }
}