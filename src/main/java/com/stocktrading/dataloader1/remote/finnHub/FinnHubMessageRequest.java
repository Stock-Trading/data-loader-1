package com.stocktrading.dataloader1.remote.finnHub;

import lombok.Builder;

@Builder
record FinnHubMessageRequest(String type,
                             String symbol) {
}
