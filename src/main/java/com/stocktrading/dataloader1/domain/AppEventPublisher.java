package com.stocktrading.dataloader1.domain;

import com.stocktrading.dataloader1.domain.event.StockPriceReceivedEvent;

public interface AppEventPublisher {

    void publishStockPriceReceivedAsAppEvent(StockPriceReceivedEvent stockPriceReceivedEvent);

}
