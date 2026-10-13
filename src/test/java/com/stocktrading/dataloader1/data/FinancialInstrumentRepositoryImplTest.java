package com.stocktrading.dataloader1.data;

import com.stocktrading.dataloader1.domain.model.FinancialInstrumentModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
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
        assertEquals(List.of("AAPL"), repository.findAllSymbols());
    }

    @Test
    @DisplayName("""
            given an instrument with the same name and symbol already saved,
            when the same instrument is saved again,
            then it should return the existing one and not add a duplicate
            """)
    void shouldReturnExistingWhenNameAndSymbolMatch() {
        // given
        FinancialInstrumentModel first = repository.save(
                FinancialInstrumentModel.builder().name("Apple").symbol("AAPL").build());

        // when
        FinancialInstrumentModel second = repository.save(
                FinancialInstrumentModel.builder().name("Apple").symbol("AAPL").build());

        // then
        assertEquals(first.id(), second.id());
        assertEquals(1, repository.findAll().size());
    }

    @Test
    @DisplayName("""
            given an instrument already saved,
            when a model with the same name and symbol but a different id is saved,
            then the id should be ignored and the existing instrument returned
            """)
    void shouldIgnoreIdWhenLookingForExisting() {
        // given
        FinancialInstrumentModel first = repository.save(
                FinancialInstrumentModel.builder().name("Apple").symbol("AAPL").build());

        // when
        FinancialInstrumentModel result = repository.save(
                FinancialInstrumentModel.builder().id(999L).name("Apple").symbol("AAPL").build());

        // then
        assertEquals(first.id(), result.id());
        assertFalse(repository.existsById(999L));
        assertEquals(1, repository.findAll().size());
    }

    @Test
    @DisplayName("""
            given an instrument already saved,
            when an instrument with the same name but a different symbol is saved,
            then it should be stored as a separate instrument
            """)
    void shouldSaveWhenOnlyNameMatches() {
        // given
        FinancialInstrumentModel first = repository.save(
                FinancialInstrumentModel.builder().name("Apple").symbol("AAPL").build());

        // when
        FinancialInstrumentModel second = repository.save(
                FinancialInstrumentModel.builder().name("Apple").symbol("APC").build());

        // then
        assertNotEquals(first.id(), second.id());
        assertEquals(2, repository.findAll().size());
    }

    @Test
    @DisplayName("""
            given several new instruments,
            when they are saved,
            then each should get a unique incrementing id
            """)
    void shouldGenerateUniqueIds() {
        // given
        FinancialInstrumentModel apple = FinancialInstrumentModel.builder().name("Apple").symbol("AAPL").build();
        FinancialInstrumentModel microsoft = FinancialInstrumentModel.builder().name("Microsoft").symbol("MSFT").build();

        // when
        FinancialInstrumentModel savedApple = repository.save(apple);
        FinancialInstrumentModel savedMicrosoft = repository.save(microsoft);

        // then
        assertEquals(1L, savedApple.id());
        assertEquals(2L, savedMicrosoft.id());
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
            given a saved instrument,
            when it is deleted by id,
            then it should no longer exist by id, name or symbol
            """)
    void shouldDeleteById() {
        // given
        FinancialInstrumentModel saved = repository.save(
                FinancialInstrumentModel.builder().name("Apple").symbol("AAPL").build());

        // when
        repository.deleteById(saved.id());

        // then
        assertFalse(repository.existsById(saved.id()));
        assertFalse(repository.existsByName("Apple"));
        assertFalse(repository.existsBySymbol("AAPL"));
    }

    @Test
    @DisplayName("""
            given a deleted instrument,
            when the same instrument is saved again,
            then it should be stored with a new id
            """)
    void shouldNotReuseIdAfterDeletion() {
        // given
        FinancialInstrumentModel first = repository.save(
                FinancialInstrumentModel.builder().name("Apple").symbol("AAPL").build());
        repository.deleteById(first.id());

        // when
        FinancialInstrumentModel second = repository.save(
                FinancialInstrumentModel.builder().name("Apple").symbol("AAPL").build());

        // then
        assertNotEquals(first.id(), second.id());
        assertEquals(1, repository.findAll().size());
    }

    @Test
    @DisplayName("""
            given a saved instrument,
            when checking existence by id, name and symbol,
            then it should return true for known and false for unknown values
            """)
    void shouldCheckExistence() {
        // given
        FinancialInstrumentModel saved = repository.save(
                FinancialInstrumentModel.builder().name("Apple").symbol("AAPL").build());

        // when
        boolean knownById = repository.existsById(saved.id());
        boolean knownByName = repository.existsByName("Apple");
        boolean knownBySymbol = repository.existsBySymbol("AAPL");

        // then
        assertTrue(knownById);
        assertTrue(knownByName);
        assertTrue(knownBySymbol);
        assertFalse(repository.existsById(999L));
        assertFalse(repository.existsByName("unknown"));
        assertFalse(repository.existsBySymbol("unknown"));
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
        assertTrue(repository.findById(1L).isEmpty());
        assertTrue(repository.findBySymbol("unknown").isEmpty());
    }

    @Test
    @DisplayName("""
            given a saved instrument,
            when the list returned by findAll is modified,
            then it should throw and leave repository content untouched
            """)
    void shouldReturnImmutableCopyFromFindAll() {
        // given
        repository.save(FinancialInstrumentModel.builder().name("Apple").symbol("AAPL").build());
        List<FinancialInstrumentModel> all = repository.findAll();

        // when
        // then
        assertThrows(UnsupportedOperationException.class, all::clear);
        assertEquals(1, repository.findAll().size());
    }

    @Test
    @DisplayName("""
            given an instrument with missing name and symbol,
            when looking up and deleting instruments by name and symbol,
            then it should handle the missing fields without throwing
            """)
    void shouldHandleMissingNameAndSymbol() {
        // given
        repository.save(FinancialInstrumentModel.builder().name("Apple").symbol("AAPL").build());
        repository.save(FinancialInstrumentModel.builder().build());
        repository.save(FinancialInstrumentModel.builder().name("Microsoft").symbol("MSFT").build());

        // when
        var byName = repository.findByName("Microsoft");
        var bySymbol = repository.findBySymbol("MSFT");
        repository.deleteByName("Apple");
        repository.deleteBySymbol("MSFT");

        // then
        assertEquals("Microsoft", byName.orElseThrow().name());
        assertEquals("MSFT", bySymbol.orElseThrow().symbol());
        assertTrue(repository.findByName("Apple").isEmpty());
        assertTrue(repository.findBySymbol("MSFT").isEmpty());
        assertEquals(1, repository.findAll().size());
        assertNull(repository.findAll().getFirst().name());
        assertNull(repository.findAll().getFirst().symbol());
    }
}
