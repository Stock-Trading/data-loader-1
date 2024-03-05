package com.stocktrading.dataloader1.controller;

import com.stocktrading.dataloader1.eventPublisher.StockDataEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/stockData")
@Log4j2
@RequiredArgsConstructor
class StockDataTempController {

    private final StockDataEventPublisher publisher;

    @PostMapping("publish")
    void publishStockDataEvent() {
        log.info("Received request to publish stockInfo");
        publisher.publishTimeSeriesDailyEvent("ibm");
    }
}