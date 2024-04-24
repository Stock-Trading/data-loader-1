package com.stocktrading.dataloader1.domain.model;

import lombok.Builder;

@Builder
public record FinancialInstrumentModel(Long id,
                                       String name,
                                       String symbol) {

}
