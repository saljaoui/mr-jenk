package com.buy01.products.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.buy01.products.dto.ProductRequest;
import com.buy01.products.dto.ProductResponse;
import com.buy01.products.exception.ProductNotFoundException;
import com.buy01.products.model.Product;
import com.buy01.products.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Page<ProductResponse> findAll(String userId, Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(product -> toResponse(product, userId));
    }

    public ProductResponse findById(String id, String userId) {
        Product product = getProductOrThrow(id);
        System.out.println("\nuserId: " + userId + ", product.getUserId(): " + product.getUserId() + "\n");
        return toResponse(product, userId);
    }

    public Page<ProductResponse> findByUserId(String userId, Pageable pageable) {
        return productRepository.findByUserId(userId, pageable)
                .map(product -> toResponse(product, userId));
    }

    public ProductResponse create(ProductRequest request, String userId) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .userId(userId)
                .build();

        Product saved = productRepository.save(product);
        return toResponse(saved, userId);
    }

    public ProductResponse update(String id, ProductRequest request, String userId) {
        Product product = getProductOrThrow(id);
        checkOwnership(product, userId);

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());

        Product saved = productRepository.save(product);
        return toResponse(saved, userId);
    }

    public void delete(String id, String userId) {
        Product product = getProductOrThrow(id);
        checkOwnership(product, userId);
        productRepository.delete(product);
    }

    // ---- helpers ----

    private Product getProductOrThrow(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
    }

    private void checkOwnership(Product product, String userId) {
        if (!product.getUserId().equals(userId)) {
            // throw new UnauthorizedActionException("You do not own this product");
        }
    }

    private ProductResponse toResponse(Product product, String userId) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .owner(product.getUserId().equals(userId))
                .build();
    }
}

// @Service
// @RequiredArgsConstructor
// @Slf4j
// public class ProductService {

// private static final String PRODUCT_NOT_FOUND = "Product not found";
// private static final String ACCESS_DENIED = "You are not allowed to access
// this product";

// private final ProductRepository productRepository;
// private final ProductEventProducer eventProducer;

// public Product createProduct(ProductRequest request, String userId) {
// Product product = Product.builder()
// .name(request.getName())
// .description(request.getDescription())
// .price(request.getPrice())
// .quantity(request.getQuantity())
// .userId(userId)
// .build();

// return this.productRepository.save(product);
// }

// public Product updateProduct(
// String productId,
// ProductRequest request,
// Authentication authentication) {

// Product product = this.findByIdOrThrow(productId);
// this.verifyOwnership(product, authentication);

// if (request.getName() != null) {
// product.setName(request.getName());
// }

// if (request.getDescription() != null) {
// product.setDescription(request.getDescription());
// }

// if (request.getPrice() != null) {
// product.setPrice(request.getPrice());
// }

// if (request.getQuantity() != null) {
// product.setQuantity(request.getQuantity());
// }

// return this.productRepository.save(product);
// }

// public void deleteProduct(String productId, Authentication authentication) {
// Product product = this.findByIdOrThrow(productId);
// this.verifyOwnership(product, authentication);

// this.productRepository.delete(product);

// try {
// this.eventProducer.sendProductDeletedEvent(
// new ProductDeletedEvent(
// productId,
// product.getUserId(),
// Instant.now()))
// .join();

// log.info("Product {} deleted successfully", productId);

// } catch (RuntimeException ex) {

// log.error(
// "Failed to publish ProductDeletedEvent for product {}. Restoring product.",
// productId,
// ex);

// try {
// this.productRepository.save(product);
// } catch (Exception restoreEx) {
// log.error("Failed to restore deleted product {}", productId, restoreEx);
// ex.addSuppressed(restoreEx);
// }

// throw new IllegalStateException(
// "Failed to publish product deleted event",
// ex.getCause() != null ? ex.getCause() : ex);
// }
// }

// public ProductResponse getProduct(String productId, Authentication
// authentication) {
// Product product = this.findByIdOrThrow(productId);

// System.out.println("ProductService.getProduct: productId=" + productId + ",
// product=" + product);
// System.out.println("ProductService.getProduct: authentication=" +
// authentication);

// ProductResponse response = ProductResponse.toResponse(product);
// response.setOwner(this.isOwner(product, authentication));

// return response;
// }

// public List<Product> getProducts() {
// return this.productRepository.findAll();
// }

// public List<Product> getProductsOwnedBy(String ownerId) {
// return this.productRepository.findAllByUserId(ownerId);
// }

// private Product findByIdOrThrow(String productId) {
// return this.productRepository.findById(productId)
// .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND));
// }

// private void verifyOwnership(Product product, Authentication authentication)
// {
// if (!this.isOwner(product, authentication)) {
// throw new ForbiddenException(ACCESS_DENIED);
// }
// }

// private boolean isOwner(Product product, Authentication authentication) {
// return authentication != null
// && authentication.getName() != null
// && product.getUserId() != null
// && product.getUserId().equals(authentication.getName());
// }

// public boolean isOwner(String productId, String userId) {
// Product product = findByIdOrThrow(productId);

// return product.getUserId() != null
// && product.getUserId().equals(userId);
// }
// }