package com.stocktrading.dataloader1.remote.restApi;

import lombok.Builder;

@Builder
record FinancialInstrumentSubscribeRequestDto(String name,
                                              String symbol) {
}
