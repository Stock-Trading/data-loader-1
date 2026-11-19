package com.stocktrading.dataloader1.remote.finnhub;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

record FinnHubFinancialInstrumentPriceResponse(@JsonProperty("p") Double price,
                                               @JsonProperty("s") String symbol,
                                               @JsonProperty("t") Long timeStampUnixMili,
                                               @JsonProperty("v") Double volume,
                                               @JsonProperty("c") List<Integer> tradeCondition) {
}
