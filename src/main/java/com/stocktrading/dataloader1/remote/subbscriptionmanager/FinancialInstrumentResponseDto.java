package com.stocktrading.dataloader1.remote.subbscriptionmanager;

import lombok.Builder;

@Builder
record FinancialInstrumentResponseDto(String name,
                                      String symbol) {
}
