package com.stocktrading.dataloader1.domain;

import java.util.List;

public interface FinancialInstrumentRepository {

    FinancialInstrumentModel findById(Long id);

    FinancialInstrumentModel save(FinancialInstrumentModel financialInstrumentModel);

    List<FinancialInstrumentModel> findAll();

    boolean existsByName(String name);

    boolean existsBySymbol(String symbol);
}
