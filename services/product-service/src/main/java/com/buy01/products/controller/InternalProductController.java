package com.buy01.products.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.buy01.products.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/products")
@RequiredArgsConstructor
public class InternalProductController {

    private final ProductService productService;

    // @GetMapping("/{productId}/ownership")
    // public boolean checkOwnership(
    //         @PathVariable String productId,
    //         @RequestHeader("X-User-Id") String userId
    // ) {
    //     boolean result = productService.isOwner(productId, userId);
    //     return result;
    // }
}