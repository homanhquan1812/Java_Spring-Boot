package org.homanhquan.productservice.service.helper.product;

import lombok.RequiredArgsConstructor;
import org.homanhquan.productservice.entity.Brand;
import org.homanhquan.productservice.entity.Product;
import org.homanhquan.productservice.exception.ResourceNotFoundException;
import org.homanhquan.productservice.projection.ProductProjection;
import org.homanhquan.productservice.repository.BrandRepository;
import org.homanhquan.productservice.repository.ProductRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductServiceImplHelper {

    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;

    public Brand findBrandById(Long brandId) {
        return brandRepository.findById(brandId)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + brandId));
    }

    public Product findProductById(Long productId){
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
    }

    public ProductProjection findProductProjectionById(Long productId) {
        return productRepository.findProductByIdWithBrandName(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
    }
}
