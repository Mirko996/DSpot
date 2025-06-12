package com.SimpleSoft.dspot.DSpot.dto.delivery;

import com.SimpleSoft.dspot.DSpot.enums.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class DeliveryResponse {
    private Long id;
    private Long orderId;
    private String storeName;
    private DeliveryStatus status;
    private String proof;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
