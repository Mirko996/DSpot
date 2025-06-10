package com.SimpleSoft.dspot.DSpot.dto.order;

import lombok.Data;
import java.util.List;

@Data
public class CreateOrderRequest {
    private Long storeUserId;
    private Long salesRepId; // Optional
    private List<OrderItemRequest> products;
}
