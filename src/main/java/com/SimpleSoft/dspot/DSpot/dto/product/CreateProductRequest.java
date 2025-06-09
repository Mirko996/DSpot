package com.SimpleSoft.dspot.DSpot.dto.product;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateProductRequest {
    private String name;
    private String description;
    private String sku;
    private BigDecimal price;
    private Integer stockQuantity;
    private String imageUrl;
}
