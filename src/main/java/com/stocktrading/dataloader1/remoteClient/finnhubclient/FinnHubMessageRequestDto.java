package com.stocktrading.dataloader1.remoteClient.finnhubclient;

import lombok.Builder;

@Builder
public record FinnHubMessageRequestDto(FinnHubMessageType type,
                                       String content) {
}
