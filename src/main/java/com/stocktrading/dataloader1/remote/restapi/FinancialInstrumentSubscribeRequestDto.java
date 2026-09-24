package com.stocktrading.dataloader1.remote.restapi;

import lombok.Builder;

@Builder
record FinancialInstrumentSubscribeRequestDto(String name,
                                              String symbol) {
}
