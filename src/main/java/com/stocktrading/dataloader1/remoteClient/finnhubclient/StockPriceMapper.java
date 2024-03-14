package com.stocktrading.dataloader1.remoteClient.finnhubclient;

import com.stocktrading.dataloader1.domain.StockPriceModel;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class StockPriceMapper {
    StockPriceModel mapToModel(StockPriceDto stockPriceDto) {
        return StockPriceModel.builder()
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
