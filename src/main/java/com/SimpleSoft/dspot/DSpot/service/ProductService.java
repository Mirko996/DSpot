package com.SimpleSoft.dspot.DSpot.service;

import com.SimpleSoft.dspot.DSpot.domain.OrderItem;
import com.SimpleSoft.dspot.DSpot.domain.Product;
import com.SimpleSoft.dspot.DSpot.dto.product.CreateProductRequest;
import com.SimpleSoft.dspot.DSpot.dto.product.ProductResponse;
import com.SimpleSoft.dspot.DSpot.dto.product.UpdateProductRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct(CreateProductRequest request);

    Page<ProductResponse> listProducts(int page, int size);

    ProductResponse updateProduct(Long id, UpdateProductRequest request);

    void deleteProduct(Long id);

    void setProductVisibility(Long productId, boolean hidden);

    Page<ProductResponse> listProducts(int page, int size, String search, Boolean inStock);

    Product reserveProductStock(Long productId, int quantity);

    void finalizeDeliveryStock(List<OrderItem> items);

    void releaseReservedStock(List<OrderItem> items);

    default ProductResponse toResponse(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getSku(),
                p.getPrice(),
                p.getStockQuantity(),
                p.getImageUrl(),
                p.isHidden()
        );
    }
}
