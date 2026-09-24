package com.stocktrading.dataloader1.remote.kafkaeventpublisher;

public class EventPublishingRuntimeException extends RuntimeException {

    public EventPublishingRuntimeException(String message) {
        super(message);
    }
}
