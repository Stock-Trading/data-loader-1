package com.stocktrading.dataloader1.domain.queue;

import com.stocktrading.dataloader1.domain.model.FinancialInstrumentPriceModel;
import org.springframework.stereotype.Component;

/// Producer of price events (price at given time)
@Component
public class FinancialInstrumentPriceProducer {

    private final FinancialInstrumentPriceQueue queue;

    public FinancialInstrumentPriceProducer(FinancialInstrumentPriceQueue queue) {
        this.queue = queue;
    }

    public void produce(FinancialInstrumentPriceModel priceModel) {
        queue.put(priceModel);
    }

}
