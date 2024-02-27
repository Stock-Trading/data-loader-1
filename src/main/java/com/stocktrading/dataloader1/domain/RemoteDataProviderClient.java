package com.stocktrading.dataloader1.domain;


public interface RemoteDataProviderClient {

    TimeSeriesDaily getTimeSeriesDailyFromAlphaVantage(String stockSymbol);
}
