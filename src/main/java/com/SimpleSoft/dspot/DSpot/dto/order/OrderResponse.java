package com.SimpleSoft.dspot.DSpot.dto.order;

import com.SimpleSoft.dspot.DSpot.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private OrderStatus status;
    private boolean isLocked;
    private String storeUserName;
    private String salesRepName;
    private List<OrderItemResponse> items;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
