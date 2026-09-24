package com.stocktrading.dataloader1.remote.restapi;

import lombok.Builder;

@Builder
record FinancialInstrumentUnsubscribeResponseDto(Long id,
                                                 String name,
                                                 String symbol) {
}
