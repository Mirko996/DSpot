package com.SimpleSoft.dspot.DSpot.dto.order;

import lombok.Data;

@Data
public class OrderItemRequest {
    private Long productId;
    private Integer quantity;
}
