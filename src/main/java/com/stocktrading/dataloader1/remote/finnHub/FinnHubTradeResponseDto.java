package com.stocktrading.dataloader1.remote.finnHub;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record FinnHubTradeResponseDto(
        @JsonProperty("data") List<FinancialInstrumentPriceDto> dataList,
        @JsonProperty("type") String type
) {
}
