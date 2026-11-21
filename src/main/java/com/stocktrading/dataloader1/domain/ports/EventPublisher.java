package com.stocktrading.dataloader1.domain.ports;


import com.stocktrading.dataloader1.domain.model.FinancialInstrumentPriceModel;

/// Publishes events externally
public interface EventPublisher {

    void publishFinancialInstrumentPriceInfoEvent(FinancialInstrumentPriceModel financialInstrumentPriceModel);

}
