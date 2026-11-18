package com.stocktrading.dataloader1.remote.kafkaeventpublisher;

import com.stocktrading.dataloader1.domain.model.FinancialInstrumentPriceModel;
import com.stocktrading.dataloader1.domain.ports.EventPublisher;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.stocktrading.dataloader1.remote.kafkaeventpublisher.EventTopic.STOCK_INFO;

@Component
@Log4j2
class StockDataEventPublisher implements EventPublisher {

    private final KafkaTemplate<String, StockPriceEventDto> kafkaTemplate;
    private final StockPriceEventMapper mapper;
    private final TaskExecutor kafkaPublisherExecutor;

    StockDataEventPublisher(KafkaTemplate<String, StockPriceEventDto> kafkaTemplate,
                            StockPriceEventMapper mapper,
                            TaskExecutor kafkaPublisherExecutor) {
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = mapper;
        this.kafkaPublisherExecutor = kafkaPublisherExecutor;
    }

    @Override
    public void publishFinancialInstrumentPriceInfoEvent(FinancialInstrumentPriceModel financialInstrumentPriceModel) {
        send(financialInstrumentPriceModel);
    }

    private void send(FinancialInstrumentPriceModel financialInstrumentPriceModel) {
        kafkaPublisherExecutor.execute(() -> {
            EventTopic topic = STOCK_INFO;
            log.info("Sending {} to Kafka broker", financialInstrumentPriceModel);
            kafkaTemplate.send(topic.getTopicName(), financialInstrumentPriceModel.symbol(), mapper.mapToDto(financialInstrumentPriceModel))
                    .whenComplete((ignored, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish Kafka event to topic {} for symbol {}",
                                    topic, financialInstrumentPriceModel.symbol(), ex);
                        }
                    });
        });
    }

}
