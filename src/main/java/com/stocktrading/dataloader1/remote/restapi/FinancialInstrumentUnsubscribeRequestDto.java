package com.stocktrading.dataloader1.remote.restapi;

import lombok.Builder;

@Builder
record FinancialInstrumentUnsubscribeRequestDto(Long id,
                                                String name,
                                                String symbol) {
}
