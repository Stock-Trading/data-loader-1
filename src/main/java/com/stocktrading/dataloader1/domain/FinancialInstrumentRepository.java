package com.stocktrading.dataloader1.domain;

import java.util.List;

public interface FinancialInstrumentRepository {

    FinancialInstrumentModel findById(Long id);

    FinancialInstrumentModel findByName(String name);

    FinancialInstrumentModel findBySymbol(String symbol);

    List<FinancialInstrumentModel> findAll();

    FinancialInstrumentModel save(FinancialInstrumentModel financialInstrumentModel);

    void deleteById(Long id);

    void deleteByName(String name);

    void deleteBySymbol(String symbol);

    boolean existsById(Long id);

    boolean existsByName(String name);

    boolean existsBySymbol(String symbol);
}
