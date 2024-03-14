package com.stocktrading.dataloader1.domain;


import java.util.List;

public interface EventPublisher {

    void publishLatestStockInfoEvent(List<StockPriceModel> stockPriceModel);

}
