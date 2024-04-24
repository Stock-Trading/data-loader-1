package com.stocktrading.dataloader1.domain.ports;


import com.stocktrading.dataloader1.domain.model.FinancialInstrumentPriceModel;

import java.util.List;

public interface KafkaEventPublisher {

    void publishFinancialInstrumentPriceInfoEvent(List<FinancialInstrumentPriceModel> financialInstrumentPriceModel);

}
