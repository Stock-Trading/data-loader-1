package com.stocktrading.dataloader1.eventPublisher;

import com.stocktrading.dataloader1.remoteClient.dataProvider.RemoteDataProviderClient;
import com.stocktrading.dataloader1.remoteClient.dataProvider.TimeSeriesDaily;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Log4j2
public class StockDataEventPublisher {

    private KafkaTemplate<String, TimeSeriesDaily> kafkaTemplate;
    private RemoteDataProviderClient remoteDataProviderClient;

    public void publishTimeSeriesDailyEvent(String stockSymbol) {
        final String topic = "stockInfo";
        try {
            log.info("Trying to send time series daily about \"{}\" to the topic \"{}\"", stockSymbol.toUpperCase(), topic);
            kafkaTemplate.send(topic, remoteDataProviderClient.getTimeSeriesDailyFromAlphaVantage(stockSymbol));
        } catch (Exception e) {
            throw new EventPublishingRuntimeException("Exception was thrown while trying to publish an event: " + e);
        }
    }


}
