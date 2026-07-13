package com.buy01.products.service;

import java.time.Instant;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.buy01.events.product.ProductDeletedEvent;
import com.buy01.products.dto.ProductRequest;
import com.buy01.products.dto.ProductResponse;
import com.buy01.products.exception.ForbiddenException;
import com.buy01.products.exception.ProductNotFoundException;
import com.buy01.products.model.Product;
import com.buy01.products.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private static final String PRODUCT_NOT_FOUND = "Product not found";
    private static final String ACCESS_DENIED = "You are not allowed to access this product";

    private final ProductRepository productRepository;
    private final ProductEventProducer eventProducer;

    public ProductResponse createProduct(ProductRequest request, String userId) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .userId(userId)
                .build();

        Product savedProduct = this.productRepository.save(product);
        return ProductResponse.toResponse(savedProduct);
    }

    public ProductResponse updateProduct(
            String productId,
            ProductRequest request,
            Authentication authentication) {

        Product product = this.findByIdOrThrow(productId);
        this.verifyOwnership(product, authentication);

        if (request.getName() != null) {
            product.setName(request.getName());
        }

        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }

        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }

        if (request.getQuantity() != null) {
            product.setQuantity(request.getQuantity());
        }

        Product savedProduct = this.productRepository.save(product);
        return ProductResponse.toResponse(savedProduct);
    }

    public void deleteProduct(String productId, Authentication authentication) {
        Product product = this.findByIdOrThrow(productId);
        this.verifyOwnership(product, authentication);

        this.productRepository.delete(product);

        try {
            this.eventProducer.sendProductDeletedEvent(
                    new ProductDeletedEvent(
                            productId,
                            product.getUserId(),
                            Instant.now()))
                    .join();

            log.info("Product {} deleted successfully", productId);

        } catch (RuntimeException ex) {

            log.error(
                    "Failed to publish ProductDeletedEvent for product {}. Restoring product.",
                    productId,
                    ex);

            try {
                this.productRepository.save(product);
            } catch (Exception restoreEx) {
                log.error("Failed to restore deleted product {}", productId, restoreEx);
                ex.addSuppressed(restoreEx);
            }

            throw new IllegalStateException(
                    "Failed to publish product deleted event",
                    ex.getCause() != null ? ex.getCause() : ex);
        }
    }

    public ProductResponse getProduct(String productId, Authentication authentication) {
        Product product = this.findByIdOrThrow(productId);

        ProductResponse response = ProductResponse.toResponse(product);
        response.setOwner(this.isOwner(product, authentication));

        return response;
    }

    public List<Product> getProducts() {
        return this.productRepository.findAll();
    }

    public List<Product> getProductsOwnedBy(String ownerId) {
        return this.productRepository.findAllByUserId(ownerId);
    }

    private Product findByIdOrThrow(String productId) {
        return this.productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND));
    }

    private void verifyOwnership(Product product, Authentication authentication) {
        if (!this.isOwner(product, authentication)) {
            throw new ForbiddenException(ACCESS_DENIED);
        }
    }

    private boolean isOwner(Product product, Authentication authentication) {
        return authentication != null
                && authentication.getName() != null
                && product.getUserId() != null
                && product.getUserId().equals(authentication.getName());
    }
}