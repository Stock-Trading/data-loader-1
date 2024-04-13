package com.stocktrading.dataloader1.data;

import org.springframework.data.jpa.repository.JpaRepository;

interface FinancialInstrumentJpaRepository extends JpaRepository<FinancialInstrumentEntity, Long> {

    boolean existsByName(String name);

    boolean existsBySymbol(String symbol);

}
