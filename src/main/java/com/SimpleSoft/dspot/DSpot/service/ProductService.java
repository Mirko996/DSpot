package com.SimpleSoft.dspot.DSpot.service;

import com.SimpleSoft.dspot.DSpot.domain.Product;
import com.SimpleSoft.dspot.DSpot.dto.product.CreateProductRequest;
import com.SimpleSoft.dspot.DSpot.dto.product.ProductResponse;
import com.SimpleSoft.dspot.DSpot.dto.product.UpdateProductRequest;
import org.springframework.data.domain.Page;

public interface ProductService {
    ProductResponse createProduct(CreateProductRequest request);

    Page<ProductResponse> listProducts(int page, int size);

    ProductResponse updateProduct(Long id, UpdateProductRequest request);

    void deleteProduct(Long id);

    void setProductVisibility(Long productId, boolean hidden);

    Page<ProductResponse> listProducts(int page, int size, String search, Boolean inStock);

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
