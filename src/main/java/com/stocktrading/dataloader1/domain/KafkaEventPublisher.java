package com.stocktrading.dataloader1.domain;


import com.stocktrading.dataloader1.domain.model.FinancialInstrumentPriceModel;

import java.util.List;

public interface KafkaEventPublisher {

    void publishLatestStockInfoEvent(List<FinancialInstrumentPriceModel> financialInstrumentPriceModel);

}
