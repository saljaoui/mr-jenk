package com.buy01.media.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.buy01.events.product.ProductDeletedEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductEventConsumer {

    private final MediaService mediaService;

    @KafkaListener(topics = "product-deleted", groupId = "media-service-group")
    public void handleProductEvent(ProductDeletedEvent event) {
        MediaCleanupResult cleanupResult = this.mediaService.deleteAllByProductId(event.productId());
        if (cleanupResult.hasFailures()) {
            throw new IllegalStateException(cleanupResult.summary());
        }
    }
}
