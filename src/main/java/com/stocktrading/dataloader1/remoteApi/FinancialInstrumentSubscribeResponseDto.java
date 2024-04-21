package com.stocktrading.dataloader1.remoteApi;

import lombok.Builder;

@Builder
record FinancialInstrumentSubscribeResponseDto(Long id,
                                               String name,
                                               String symbol) {
}
