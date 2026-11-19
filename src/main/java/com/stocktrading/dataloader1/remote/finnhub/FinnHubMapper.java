package com.stocktrading.dataloader1.remote.finnhub;

import com.stocktrading.dataloader1.domain.model.FinancialInstrumentPriceModel;
import com.stocktrading.dataloader1.domain.ports.DataMapper;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
class FinnHubMapper implements DataMapper<FinancialInstrumentPriceModel> {

    private final ObjectMapper objectMapper;

    FinnHubMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public List<FinancialInstrumentPriceModel> map(String finnHubResponseJson) {
        FinnHubResponseDto responseDto = objectMapper.readValue(finnHubResponseJson, FinnHubResponseDto.class);
        if (responseDto.dataList() == null) {
            return List.of();
        }
        return responseDto.dataList()
                .stream()
                .map(this::mapToModel)
                .toList();
    }

    private FinancialInstrumentPriceModel mapToModel(FinnHubFinancialInstrumentPriceResponse finnHubFinancialInstrumentPriceResponse) {
        return FinancialInstrumentPriceModel.builder()
                .symbol(finnHubFinancialInstrumentPriceResponse.symbol())
                .priceUSD(BigDecimal.valueOf(finnHubFinancialInstrumentPriceResponse.price()))
                .dateTime(transformToLocalDateTime(finnHubFinancialInstrumentPriceResponse.timeStampUnixMili()))
                .build();
    }

    private LocalDateTime transformToLocalDateTime(Long unixTimestampMili) {
        Instant instant = Instant.ofEpochMilli(unixTimestampMili);
        return LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
    }

}
