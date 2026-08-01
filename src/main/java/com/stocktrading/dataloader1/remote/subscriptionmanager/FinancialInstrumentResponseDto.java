package com.stocktrading.dataloader1.remote.subscriptionmanager;

import lombok.Builder;

@Builder
record FinancialInstrumentResponseDto(String name,
                                      String symbol) {
}
