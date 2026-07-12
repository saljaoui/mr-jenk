package com.buy01.events.product;

import java.time.Instant;

public record ProductDeletedEvent(
        String productId,
        String deletedBy,
        Instant deletedAt
) {
}
