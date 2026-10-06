package com.stocktrading.dataloader1.data;

import com.stocktrading.dataloader1.domain.model.FinancialInstrumentModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FinancialInstrumentRepositoryImplTest {

    private final FinancialInstrumentRepositoryImpl repository = new FinancialInstrumentRepositoryImpl();

    @Test
    @DisplayName("""
            given a new instrument,
            when it is saved,
            then it should get an id and be findable by id, name and symbol
            """)
    void should() {
        // given
        FinancialInstrumentModel model = FinancialInstrumentModel.builder().name("Apple").symbol("AAPL").build();

        // when
        FinancialInstrumentModel saved = repository.save(model);

        // then
        assertNotNull(saved.id());
        assertEquals(saved, repository.findById(saved.id()).orElseThrow());
        assertEquals(saved, repository.findByName("Apple").orElseThrow());
        assertEquals(saved, repository.findBySymbol("AAPL").orElseThrow());
        assertEquals(java.util.List.of("AAPL"), repository.findAllSymbols());
    }

    @Test
    @DisplayName("""
            given a saved instrument,
            when it is deleted by symbol,
            then it should no longer exist
            """)
    void shouldDelete() {
        // given
        FinancialInstrumentModel saved = repository.save(
                FinancialInstrumentModel.builder().name("Apple").symbol("AAPL").build());

        // when
        repository.deleteBySymbol("AAPL");

        // then
        assertFalse(repository.existsById(saved.id()));
        assertFalse(repository.existsByName("Apple"));
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    @DisplayName("""
            given an empty repository,
            when searching for unknown instrument,
            then it should return empty optional
            """)
    void shouldReturnEmpty() {
        // given empty repository

        // when
        var result = repository.findByName("unknown");

        // then
        assertTrue(result.isEmpty());
    }
}
