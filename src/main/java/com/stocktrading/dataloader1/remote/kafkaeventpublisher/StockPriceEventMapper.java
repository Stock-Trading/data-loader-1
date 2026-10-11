package com.stocktrading.dataloader1.remote.kafkaeventpublisher;

import com.stocktrading.dataloader1.domain.model.FinancialInstrumentPriceModel;
import org.springframework.stereotype.Component;

@Component
class StockPriceEventMapper {

    StockPriceEventDto mapToDto(FinancialInstrumentPriceModel model) {
        return StockPriceEventDto.builder()
                .symbol(model.symbol())
                .priceUSD(model.priceUSD())
                .dateTime(model.dateTime())
                .build();
    }
}
