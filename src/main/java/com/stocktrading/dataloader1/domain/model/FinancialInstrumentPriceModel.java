package com.stocktrading.dataloader1.domain.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record FinancialInstrumentPriceModel(
        String symbol,
        BigDecimal priceUSD,
        LocalDateTime dateTime
) {

}