package com.stocktrading.dataloader1.remote.finnHub;

import com.stocktrading.dataloader1.domain.FinancialInstrumentPriceModel;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class StockPriceMapper {
    FinancialInstrumentPriceModel mapToModel(StockPriceDto stockPriceDto) {
        return FinancialInstrumentPriceModel.builder()
                .symbol(stockPriceDto.symbol())
                .priceUSD(stockPriceDto.price())
                .dateTime(transformToLocalDateTime(stockPriceDto.timeStampUnixMili()))
                .build();
    }

    private LocalDateTime transformToLocalDateTime(Long unixTimestampMili) {
        Instant instant = Instant.ofEpochMilli(unixTimestampMili);
        return LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
    }
}
