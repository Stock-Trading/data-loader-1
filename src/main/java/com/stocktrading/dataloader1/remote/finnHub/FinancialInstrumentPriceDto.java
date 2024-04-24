package com.stocktrading.dataloader1.remote.finnHub;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record FinancialInstrumentPriceDto(
        @JsonProperty("p") Double price,
        @JsonProperty("s") String symbol,
        @JsonProperty("t") Long timeStampUnixMili,
        @JsonProperty("v") Double volume,
        @JsonProperty("c") List<Integer> tradeCondition

) {
}
