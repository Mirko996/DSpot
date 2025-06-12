package com.SimpleSoft.dspot.DSpot.service.impl;

import com.SimpleSoft.dspot.DSpot.domain.Distributor;
import com.SimpleSoft.dspot.DSpot.domain.OrderItem;
import com.SimpleSoft.dspot.DSpot.domain.Product;
import com.SimpleSoft.dspot.DSpot.dto.product.CreateProductRequest;
import com.SimpleSoft.dspot.DSpot.dto.product.ProductResponse;
import com.SimpleSoft.dspot.DSpot.dto.product.UpdateProductRequest;
import com.SimpleSoft.dspot.DSpot.exception.ServiceException;
import com.SimpleSoft.dspot.DSpot.repository.DistributorRepository;
import com.SimpleSoft.dspot.DSpot.repository.ProductRepository;
import com.SimpleSoft.dspot.DSpot.security.AuthUtils;
import com.SimpleSoft.dspot.DSpot.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final DistributorRepository distributorRepository;

    @Override
    public ProductResponse createProduct(CreateProductRequest request) {
        Long distributorId = AuthUtils.getCurrentDistributorId();
        Distributor distributor = distributorRepository.findById(distributorId)
                .orElseThrow(() -> new ServiceException("Distributor not found"));

        if (productRepository.existsBySkuAndDistributor(request.getSku(), distributor))
            throw new ServiceException("SKU already exists for this distributor");

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .sku(request.getSku())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .imageUrl(request.getImageUrl())
                .distributor(distributor)
                .build();

        productRepository.save(product);
        return toResponse(product);
    }

    public Page<ProductResponse> listProducts(int page, int size) {
        Long distributorId = AuthUtils.getCurrentDistributorId();
        Distributor distributor = distributorRepository.findById(distributorId)
                .orElseThrow(() -> new ServiceException("Distributor not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Product> productPage;

        String role = AuthUtils.getCurrentUserRole();
        if ("ADMIN".equals(role))
            productPage = productRepository.findAllByDistributor(distributor, pageable);
        else
            productPage = productRepository.findAllByDistributorAndIsHiddenFalse(distributor, pageable);

        return productPage.map(this::toResponse);
    }

    public Page<ProductResponse> listProducts(int page, int size, String search, Boolean inStock) {
        Long distributorId = AuthUtils.getCurrentDistributorId();
        String role = AuthUtils.getCurrentUserRole();

        Distributor distributor = distributorRepository.findById(distributorId)
                .orElseThrow(() -> new ServiceException("Distributor not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        boolean includeHidden = "ADMIN".equalsIgnoreCase(role);

        Page<Product> products = productRepository.searchProducts(
                distributor,
                search != null && !search.isBlank() ? search : null,
                inStock,
                includeHidden,
                pageable
        );

        return products.map(this::toResponse);
    }

    @Transactional
    public Product reserveProductStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ServiceException("Product not found: " + productId));

        if (product.getStockQuantity() < quantity)
            throw new ServiceException("Insufficient stock for product: " + product.getName());

        product.setReservedStockQuantity(product.getReservedStockQuantity() + quantity);
        productRepository.save(product);
        return product;
    }

    @Transactional
    public void finalizeDeliveryStock(List<OrderItem> items) {
        for (OrderItem item : items) {
            Product product = item.getProduct();
            int qty = item.getQuantity();

            if (product.getReservedStockQuantity() < qty) {
                throw new ServiceException("Reserved stock mismatch for product: " + product.getName());
            }

            product.setReservedStockQuantity(product.getReservedStockQuantity() - qty);
            product.setStockQuantity(product.getStockQuantity() - qty);

            productRepository.save(product);
        }
    }

    @Transactional
    public void releaseReservedStock(List<OrderItem> items) {
        for (OrderItem item : items) {
            Product product = item.getProduct();
            int qty = item.getQuantity();

            if (product.getReservedStockQuantity() < qty) {
                throw new ServiceException("Reserved stock underflow for product: " + product.getName());
            }

            product.setReservedStockQuantity(product.getReservedStockQuantity() - qty);
            productRepository.save(product);
        }
    }

    @Override
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {
        Long distributorId = AuthUtils.getCurrentDistributorId();
        Distributor distributor = distributorRepository.findById(distributorId)
                .orElseThrow(() -> new ServiceException("Distributor not found"));

        Product product = productRepository.findByIdAndDistributor(id, distributor);
        if (product == null) {
            throw new ServiceException("Product not found or access denied");
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setImageUrl(request.getImageUrl());

        productRepository.save(product);

        return toResponse(product);
    }

    @Override
    public void deleteProduct(Long id) {
        Long distributorId = AuthUtils.getCurrentDistributorId();
        Distributor distributor = distributorRepository.findById(distributorId)
                .orElseThrow(() -> new ServiceException("Distributor not found"));

        Product product = productRepository.findByIdAndDistributor(id, distributor);
        if (product == null) {
            throw new ServiceException("Product not found or access denied");
        }

        productRepository.delete(product);
    }

    public void setProductVisibility(Long productId, boolean hidden) {
        Long distributorId = AuthUtils.getCurrentDistributorId();
        Distributor distributor = distributorRepository.findById(distributorId)
                .orElseThrow(() -> new ServiceException("Distributor not found"));

        Product product = productRepository.findByIdAndDistributor(productId, distributor);
        if (product == null) {
            throw new ServiceException("Product not found or access denied");
        }

        product.setHidden(hidden);
        productRepository.save(product);
    }

}
