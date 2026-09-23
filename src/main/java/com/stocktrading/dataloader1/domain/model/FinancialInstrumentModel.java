package com.stocktrading.dataloader1.domain.model;

import lombok.Builder;

import java.util.Objects;

@Builder
public record FinancialInstrumentModel(Long id,
                                       String name,
                                       String symbol) {

    //equals() and hashCode() overridden intentionally to exclude id field from equals
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FinancialInstrumentModel that)) return false;
        return Objects.equals(name, that.name) && Objects.equals(symbol, that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, symbol);
    }

}
