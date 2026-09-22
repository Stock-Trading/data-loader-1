package com.stocktrading.dataloader1.remote.finnHub;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

record FinnHubTradeResponseDto(@JsonProperty("data") List<FinancialInstrumentPriceResponse> dataList,
                               @JsonProperty("type") String type) {
}
