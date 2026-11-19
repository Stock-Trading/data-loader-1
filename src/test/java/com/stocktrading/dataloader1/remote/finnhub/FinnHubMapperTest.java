package com.stocktrading.dataloader1.remote.finnhub;

import com.stocktrading.dataloader1.domain.model.FinancialInstrumentPriceModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FinnHubMapperTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FinnHubMapper mapper = new FinnHubMapper(objectMapper);

    @Test
    @DisplayName("""
            given a FinnHub trade response json with a single data entry,
            when deserialize is called,
            then it should return a list with one correctly mapped price model
            """)
    void should() {
        // given
        String json = """
                {"data":[{"p":189.95,"s":"AAPL","t":1700000000000,"v":100,"c":[1]}],"type":"trade"}
                """;

        // when
        List<FinancialInstrumentPriceModel> result = mapper.map(json);

        // then
        assertEquals(1, result.size());
        FinancialInstrumentPriceModel model = result.getFirst();
        assertEquals("AAPL", model.symbol());
        assertEquals(BigDecimal.valueOf(189.95), model.priceUSD());
        assertEquals(LocalDateTime.of(2023, 11, 14, 22, 13, 20), model.dateTime());
    }

    @Test
    @DisplayName("""
            given a FinnHub trade response json with multiple data entries,
            when deserialize is called,
            then it should map each entry to a corresponding price model preserving order
            """)
    void shouldMapAllEntriesInOrder() {
        // given
        String json = """
                {"data":[{"p":189.95,"s":"AAPL","t":1700000000000,"v":100,"c":[1]},
                {"p":410.34,"s":"MSFT","t":1700000060000,"v":50,"c":[2]}],"type":"trade"}
                """;

        // when
        List<FinancialInstrumentPriceModel> result = mapper.map(json);

        // then
        assertEquals(2, result.size());
        assertEquals("AAPL", result.get(0).symbol());
        assertEquals("MSFT", result.get(1).symbol());
    }

    @Test
    @DisplayName("""
            given a FinnHub response json with an empty data array,
            when deserialize is called,
            then it should return an empty list
            """)
    void shouldReturnEmptyListWhenNoData() {
        // given
        String json = """
                {"data":[],"type":"trade"}
                """;

        // when
        List<FinancialInstrumentPriceModel> result = mapper.map(json);

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("""
            given a FinnHub response json without a data field,
            when deserialize is called,
            then it should return an empty list instead of throwing
            """)
    void shouldReturnEmptyListWhenDataFieldIsMissing() {
        // given
        String json = """
                {"type":"ping"}
                """;

        // when
        List<FinancialInstrumentPriceModel> result = mapper.map(json);

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("""
            given a malformed json payload,
            when deserialize is called,
            then it should propagate a JacksonException
            """)
    void shouldThrowWhenJsonIsMalformed() {
        // given
        String json = "not a json";

        // when
        // then
        assertThrows(JacksonException.class, () -> mapper.map(json));
    }
}
