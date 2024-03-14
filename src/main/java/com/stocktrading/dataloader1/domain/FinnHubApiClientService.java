package com.stocktrading.dataloader1.domain;

import java.util.List;

public interface FinnHubApiClientService {

        List<StockPriceModel> getStockPriceDataList(String jsonMessage);

}