package com.stocktrading.dataloader1.domain;

import com.stocktrading.dataloader1.domain.event.FinancialInstrumentPriceReceivedEvent;

public interface AppEventPublisher {

    void publishStockPriceReceivedAsAppEvent(FinancialInstrumentPriceReceivedEvent financialInstrumentPriceReceivedEvent);

}
