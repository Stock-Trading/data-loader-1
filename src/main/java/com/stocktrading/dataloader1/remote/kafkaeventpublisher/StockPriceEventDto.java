package com.stocktrading.dataloader1.remote.kafkaeventpublisher;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
record StockPriceEventDto(
        String symbol,
        BigDecimal priceUSD,
        LocalDateTime dateTime
) {

}
