package com.stocktrading.dataloader1.remoteApi;

import lombok.Builder;

@Builder
record FinancialInstrumentUnsubscribeRequestDto(Long id,
                                                String name,
                                                String symbol) {
}
