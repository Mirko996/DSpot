package com.SimpleSoft.dspot.DSpot.service.impl;

import com.SimpleSoft.dspot.DSpot.domain.Distributor;
import com.SimpleSoft.dspot.DSpot.domain.Product;
import com.SimpleSoft.dspot.DSpot.dto.product.CreateProductRequest;
import com.SimpleSoft.dspot.DSpot.dto.product.ProductResponse;
import com.SimpleSoft.dspot.DSpot.dto.product.UpdateProductRequest;
import com.SimpleSoft.dspot.DSpot.exception.ServiceException;
import com.SimpleSoft.dspot.DSpot.repository.DistributorRepository;
import com.SimpleSoft.dspot.DSpot.repository.ProductRepository;
import com.SimpleSoft.dspot.DSpot.security.AuthUtils;
import com.SimpleSoft.dspot.DSpot.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

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
