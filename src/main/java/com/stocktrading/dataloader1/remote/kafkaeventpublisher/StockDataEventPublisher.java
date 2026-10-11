package com.stocktrading.dataloader1.remote.kafkaeventpublisher;

import com.stocktrading.dataloader1.domain.event.FinancialInstrumentPriceReceivedEvent;
import com.stocktrading.dataloader1.domain.model.FinancialInstrumentPriceModel;
import com.stocktrading.dataloader1.domain.ports.EventPublisher;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.stocktrading.dataloader1.remote.kafkaeventpublisher.EventTopic.STOCK_INFO;

@Component
@Log4j2
class StockDataEventPublisher implements EventPublisher {

    private final KafkaTemplate<String, FinancialInstrumentPriceModel> kafkaTemplate;

    StockDataEventPublisher(KafkaTemplate<String, FinancialInstrumentPriceModel> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @EventListener
    @Async
    void handleFinancialInstrumentPriceReceivedEvent(FinancialInstrumentPriceReceivedEvent event) {
        List<FinancialInstrumentPriceModel> list = event.getFinancialInstrumentPriceModelList();
        list.forEach(this::publishFinancialInstrumentPriceInfoEvent);
    }

    @Override
    public void publishFinancialInstrumentPriceInfoEvent(FinancialInstrumentPriceModel financialInstrumentPriceModel) {
        EventTopic topic = STOCK_INFO;
        try {
            kafkaTemplate.send(topic.getTopicName(), financialInstrumentPriceModel.symbol(), financialInstrumentPriceModel)
                    .whenComplete((ignored, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish Kafka event to topic {} for symbol {}",
                                    topic, financialInstrumentPriceModel.symbol(), ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Failed to publish Kafka event to topic {} for symbol {}",
                    topic, financialInstrumentPriceModel.symbol(), e);
        }
    }

}
