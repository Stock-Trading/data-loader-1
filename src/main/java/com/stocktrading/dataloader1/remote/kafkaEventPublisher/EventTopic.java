package com.stocktrading.dataloader1.remote.kafkaEventPublisher;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EventTopic {

    STOCK_INFO ("stockInfo");

    private final String topicName;

}
