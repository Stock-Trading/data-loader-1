package com.stocktrading.dataloader1.domain;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
@Log4j2
public class StockPriceService implements AppEventPublisher {

    private final EventPublisher eventPublisher;

    @Override
    public void publishStockPriceReceivedAsAppEvent(StockPriceReceivedEvent stockPriceReceivedEvent) {
        eventPublisher.publishLatestStockInfoEvent(stockPriceReceivedEvent.getStockPriceModelList());
    }
}
