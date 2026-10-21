package com.stocktrading.dataloader1.remote.finnhub;

import com.stocktrading.dataloader1.domain.model.FinancialInstrumentPriceModel;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
class FinancialInstrumentPriceMapper {

    FinancialInstrumentPriceModel mapToModel(FinancialInstrumentPriceResponse financialInstrumentPriceResponse) {
        return FinancialInstrumentPriceModel.builder()
                .symbol(financialInstrumentPriceResponse.symbol())
                .priceUSD(BigDecimal.valueOf(financialInstrumentPriceResponse.price()))
                .dateTime(transformToLocalDateTime(financialInstrumentPriceResponse.timeStampUnixMili()))
                .build();
    }

    private LocalDateTime transformToLocalDateTime(Long unixTimestampMili) {
        Instant instant = Instant.ofEpochMilli(unixTimestampMili);
        return LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
    }

}
