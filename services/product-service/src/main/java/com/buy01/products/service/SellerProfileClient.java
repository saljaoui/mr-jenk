package com.buy01.products.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.buy01.products.dto.SellerPublicResponse;

@Component
public class SellerProfileClient {

    private final RestClient restClient;

    public SellerProfileClient(
            RestClient.Builder restClientBuilder,
            @Value("${buy01.user-service.url:http://localhost:8082}") String userServiceUrl) {
        this.restClient = restClientBuilder.baseUrl(userServiceUrl).build();
    }

    public SellerPublicResponse getSellerProfile(String sellerId) {
        try {
            return restClient.get()
                    .uri("/api/users/{sellerId}/seller-profile", sellerId)
                    .retrieve()
                    .body(SellerPublicResponse.class);
        } catch (RestClientException exception) {
            return null;
        }
    }
}
