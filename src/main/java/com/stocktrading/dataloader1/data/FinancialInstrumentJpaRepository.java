package com.stocktrading.dataloader1.data;

import org.springframework.data.jpa.repository.JpaRepository;

interface FinancialInstrumentJpaRepository extends JpaRepository<FinancialInstrumentEntity, Long> {

    FinancialInstrumentEntity getByName(String name);

    FinancialInstrumentEntity getBySymbol(String symbol);

    void deleteById(Long id);

    void deleteByName(String name);

    void deleteBySymbol(String symbol);

    boolean existsByName(String name);

    boolean existsBySymbol(String symbol);

}
