package com.stocktrading.dataloader1.remoteApi;

import lombok.Builder;

@Builder
record FinancialInstrumentUnsubscribeResponseDto(Long id,
                                                 String name,
                                                 String symbol) {
}
