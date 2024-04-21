package com.stocktrading.dataloader1.remote.restApi;

import lombok.Builder;

@Builder
record FinancialInstrumentResponseDto(Long id,
                                      String name,
                                      String symbol) {
}
