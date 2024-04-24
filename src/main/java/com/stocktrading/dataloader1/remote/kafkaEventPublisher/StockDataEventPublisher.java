package com.stocktrading.dataloader1.remote.kafkaEventPublisher;

import com.stocktrading.dataloader1.domain.ports.KafkaEventPublisher;
import com.stocktrading.dataloader1.domain.model.FinancialInstrumentPriceModel;
import com.stocktrading.dataloader1.domain.event.FinancialInstrumentPriceReceivedEvent;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@Log4j2
public class StockDataEventPublisher implements KafkaEventPublisher {

//    private KafkaTemplate<String, TimeSeriesDaily> kafkaTemplate;

    @EventListener
    public void handleFinancialInstrumentPriceReceivedEvent(FinancialInstrumentPriceReceivedEvent event) {
        publishFinancialInstrumentPriceInfoEvent(event.getFinancialInstrumentPriceModelList());
    }

    @Override
    public void publishFinancialInstrumentPriceInfoEvent(List<FinancialInstrumentPriceModel> financialInstrumentPriceModelList) {
//        EventTopic topic = STOCK_INFO;
//        stockPriceService.publishStockPriceEvent("AAPL");
//        try {
//            log.info("Trying to send time series daily about \"{}\" to the topic \"{}\"", stockSymbol.toUpperCase(), topic);
//            kafkaTemplate.send(topic.getTopicName(), timeSeriesDailyIBM);
//            log.info("Event published to topic {}", topic);
//        } catch (Exception e) {
//            throw new EventPublishingRuntimeException("Exception was thrown while trying to publish an event: " + e);
//        }

        log.info("Info from kafka producer. Received stockPriceModelList" + financialInstrumentPriceModelList);

    }

}
