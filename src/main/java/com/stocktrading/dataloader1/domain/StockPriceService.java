package com.stocktrading.dataloader1.domain;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
@Log4j2
public class StockPriceService implements AppEventPublisher {

    private final KafkaEventPublisher kafkaEventPublisher;

    @Override
    public void publishStockPriceReceivedAsAppEvent(StockPriceReceivedEvent stockPriceReceivedEvent) {
        kafkaEventPublisher.publishLatestStockInfoEvent(stockPriceReceivedEvent.getFinancialInstrumentPriceModelList());
    }
}

//TODO publisher eventów springowych nasłuchuje w FinnHub kliencie czy przychodzą informacje,
// kiedy przychodzą, wysyła event. Listiner eventów springowych także jest w domenie (wszystko wraca do domeny) i tam wywoływany
// jest publisher Kafkowy