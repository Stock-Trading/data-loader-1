package com.stocktrading.dataloader1.remote.kafkaEventPublisher;

public class EventPublishingRuntimeException extends RuntimeException {

    public EventPublishingRuntimeException(String message) {
        super(message);
    }
}
