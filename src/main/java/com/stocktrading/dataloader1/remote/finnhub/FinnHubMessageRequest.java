package com.stocktrading.dataloader1.remote.finnhub;

import lombok.Builder;

@Builder
record FinnHubMessageRequest(String type,
                             String symbol) {
}
