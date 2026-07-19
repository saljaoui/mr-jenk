package com.buy01.products.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.buy01.products.dto.ProductRequest;
import com.buy01.products.dto.ProductResponse;
import com.buy01.products.model.Product;
import com.buy01.products.service.ProductService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/products")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PreAuthorize("hasRole('SELLER')")
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        Product product = productService.createProduct(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(ProductResponse.toResponse(product));
    }

    @PreAuthorize("hasRole('SELLER')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable String id, @Valid @RequestBody ProductRequest request,
            Authentication authentication) {

        Product updated = this.productService.updateProduct(id, request, authentication);

        return ResponseEntity.ok(ProductResponse.toResponse(updated));
    }

    @PreAuthorize("hasRole('SELLER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable String id, Authentication authentication) {
        this.productService.deleteProduct(id, authentication);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", String.format("Product \"%s\" is deleted successfully", id)));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll() {
        return ResponseEntity.ok(
                productService.getProducts().stream()
                        .map(ProductResponse::toResponse)
                        .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable String id, Authentication authentication) {
        return ResponseEntity.ok(this.productService.getProduct(id, authentication));
    }

    @GetMapping("/ownedBy/{id}")
    public ResponseEntity<List<ProductResponse>> getOwnedBy(@PathVariable String id) {
        return ResponseEntity.ok(this.productService.getProductsOwnedBy(id).stream()
                .map(ProductResponse::toResponse)
                .toList());
    }
}