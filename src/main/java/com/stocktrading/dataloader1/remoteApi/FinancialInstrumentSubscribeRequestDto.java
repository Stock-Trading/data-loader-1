package com.stocktrading.dataloader1.remoteApi;

import lombok.Builder;

@Builder
record FinancialInstrumentSubscribeRequestDto(String name,
                                              String symbol) {
}
