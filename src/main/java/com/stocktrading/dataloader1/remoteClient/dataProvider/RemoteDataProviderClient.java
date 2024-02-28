package com.stocktrading.dataloader1.remoteClient.dataProvider;


public interface RemoteDataProviderClient {

    TimeSeriesDaily getTimeSeriesDailyFromAlphaVantage(String stockSymbol);

}
