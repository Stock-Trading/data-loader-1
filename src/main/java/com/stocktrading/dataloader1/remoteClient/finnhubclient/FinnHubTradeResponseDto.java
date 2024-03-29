package com.stocktrading.dataloader1.remoteClient.finnhubclient;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record FinnHubTradeResponseDto(
        @JsonProperty("data") List<StockPriceDto> dataList,
        @JsonProperty("type") String type
) {
}
