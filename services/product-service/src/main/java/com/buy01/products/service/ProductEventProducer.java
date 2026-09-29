package com.buy01.products.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductEventProducer {

    private static final String TOPIC = "product-deleted";

    private final KafkaTemplate<String, ProductDeletedEvent> kafkaTemplate;

    public record ProductDeletedEvent(String productId) {
    }

    public CompletableFuture<SendResult<String, ProductDeletedEvent>> sendProductDeletedEvent(
            ProductDeletedEvent event) {
        return kafkaTemplate.send(TOPIC, event.productId(), event);
    }
}