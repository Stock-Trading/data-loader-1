package com.stocktrading.dataloader1.domain.ports;

import com.stocktrading.dataloader1.domain.model.FinancialInstrumentModel;

import java.util.List;
import java.util.Optional;

public interface FinancialInstrumentRepository {

    Optional<FinancialInstrumentModel> findById(Long id);

    Optional<FinancialInstrumentModel> findByName(String name);

    Optional<FinancialInstrumentModel> findBySymbol(String symbol);

    List<FinancialInstrumentModel> findAll();

    List<String> findAllSymbols();

    FinancialInstrumentModel save(FinancialInstrumentModel financialInstrumentModel);

    void deleteById(Long id);

    void deleteByName(String name);

    void deleteBySymbol(String symbol);

    boolean existsById(Long id);

    boolean existsByName(String name);

    boolean existsBySymbol(String symbol);
}
