package com.buy01.products.event;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
public class ProductEventPublisher {

    private final KafkaTemplate<String, ProductDeletedEvent> kafkaTemplate;

    public ProductEventPublisher(KafkaTemplate<String, ProductDeletedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public record ProductDeletedEvent(String productId) {
    }

    public void publishProductDeleted(String productId) {
        ProductDeletedEvent event = new ProductDeletedEvent(productId);
        kafkaTemplate.send("product-deleted", productId, event);
    }
}