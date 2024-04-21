package com.stocktrading.dataloader1.remoteApi;

import lombok.Builder;

@Builder
record FinancialInstrumentResponseDto(Long id,
                                      String name,
                                      String symbol) {
}
