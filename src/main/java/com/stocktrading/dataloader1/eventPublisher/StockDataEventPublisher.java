package com.stocktrading.dataloader1.eventPublisher;

import com.stocktrading.dataloader1.remoteClient.dataProvider.RemoteDataProviderClient;
import com.stocktrading.dataloader1.remoteClient.dataProvider.TimeSeriesDaily;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.stocktrading.dataloader1.eventPublisher.EventTopic.STOCK_INFO;

@Component
@AllArgsConstructor
@Log4j2
public class StockDataEventPublisher {

    private KafkaTemplate<String, TimeSeriesDaily> kafkaTemplate;
    private RemoteDataProviderClient remoteDataProviderClient;

    public void publishTimeSeriesDailyEvent(String stockSymbol) {
        EventTopic topic = STOCK_INFO;
        TimeSeriesDaily timeSeriesDailyIBM = remoteDataProviderClient.getTimeSeriesDailyFromAlphaVantage(stockSymbol);
        try {
            log.info("Trying to send time series daily about \"{}\" to the topic \"{}\"", stockSymbol.toUpperCase(), topic);
            kafkaTemplate.send(topic.name(), timeSeriesDailyIBM);
            log.info("Event published to topic {}", topic);
        } catch (Exception e) {
            throw new EventPublishingRuntimeException("Exception was thrown while trying to publish an event: " + e);
        }
    }

}
