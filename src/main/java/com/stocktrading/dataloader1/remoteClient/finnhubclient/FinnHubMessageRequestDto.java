package com.stocktrading.dataloader1.remoteClient.finnhubclient;

import lombok.Builder;

@Builder
public record FinnHubMessageRequestDto(String type,
                                       String symbol) {
}
