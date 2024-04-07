package com.stocktrading.dataloader1.domain;


import java.util.List;

public interface KafkaEventPublisher {

    void publishLatestStockInfoEvent(List<StockPriceModel> stockPriceModel);

}
