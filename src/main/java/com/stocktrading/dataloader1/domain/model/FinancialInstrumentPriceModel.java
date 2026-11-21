package com.stocktrading.dataloader1.domain.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/// Represents price of some Financial Instrument at given point in time
///
/// @param symbol   also known as ticker, e.g. AMZN
/// @param priceUSD price in US Dollars
/// @param dateTime date and time
/// @see FinancialInstrumentModel
@Builder
public record FinancialInstrumentPriceModel(
        String symbol,
        BigDecimal priceUSD,
        LocalDateTime dateTime
) {

}