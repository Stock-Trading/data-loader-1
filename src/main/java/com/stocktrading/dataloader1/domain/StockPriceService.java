package com.stocktrading.dataloader1.domain;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Log4j2
public class StockPriceService {

    private final EventPublisher eventPublisher;

    public void publishStockPriceEvent(List<StockPriceModel> stockPriceModelList) {
        eventPublisher.publishLatestStockInfoEvent(stockPriceModelList);
    }

}
