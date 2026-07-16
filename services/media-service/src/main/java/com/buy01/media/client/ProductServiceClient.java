package com.buy01.media.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "product-service")
public interface ProductServiceClient {

    @GetMapping("/internal/products/{productId}/ownership")
    boolean checkOwnership(
            @PathVariable String productId,
            @RequestHeader("X-User-Id") String userId
    );
}