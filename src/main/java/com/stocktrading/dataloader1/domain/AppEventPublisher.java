package com.stocktrading.dataloader1.domain;

public interface AppEventPublisher {

    void publishStockPriceReceivedAsAppEvent(StockPriceReceivedEvent stockPriceReceivedEvent);

}
