package com.stocktrading.dataloader1.remote.restapi;

import lombok.Builder;

@Builder
record FinancialInstrumentResponseDto(Long id,
                                      String name,
                                      String symbol) {
}
