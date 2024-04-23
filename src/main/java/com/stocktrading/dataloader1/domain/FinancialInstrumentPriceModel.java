package com.stocktrading.dataloader1.domain;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record FinancialInstrumentPriceModel(
        String symbol,
        Double priceUSD,
        LocalDateTime dateTime
) {

}