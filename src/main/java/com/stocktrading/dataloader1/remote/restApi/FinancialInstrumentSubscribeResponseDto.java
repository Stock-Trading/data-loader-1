package com.stocktrading.dataloader1.remote.restApi;

import lombok.Builder;

@Builder
record FinancialInstrumentSubscribeResponseDto(Long id,
                                               String name,
                                               String symbol) {
}
