package com.stocktrading.dataloader1.remote.finnHub;

import lombok.Builder;

@Builder
public record FinnHubMessageRequestDto(String type,
                                       String symbol) {
}
