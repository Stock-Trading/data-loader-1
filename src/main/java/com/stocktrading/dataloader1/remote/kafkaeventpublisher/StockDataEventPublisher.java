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
import java.util.concurrent.CompletableFuture;

import static com.stocktrading.dataloader1.remote.kafkaeventpublisher.EventTopic.STOCK_INFO;

@Component
@Log4j2
class StockDataEventPublisher implements EventPublisher {

    private final KafkaTemplate<String, StockPriceEventDto> kafkaTemplate;
    private final StockPriceEventMapper mapper;

    StockDataEventPublisher(KafkaTemplate<String, StockPriceEventDto> kafkaTemplate, StockPriceEventMapper mapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = mapper;
    }

    @EventListener
    @Async(KafkaPublisherAsyncConfig.KAFKA_PUBLISHER_EXECUTOR)
    void handleFinancialInstrumentPriceReceivedEvent(FinancialInstrumentPriceReceivedEvent event) {
        List<FinancialInstrumentPriceModel> list = event.getFinancialInstrumentPriceModelList();
        CompletableFuture<?>[] futures = list.stream()
                .map(this::send)
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(futures).join();
    }

    @Override
    public void publishFinancialInstrumentPriceInfoEvent(FinancialInstrumentPriceModel financialInstrumentPriceModel) {
        send(financialInstrumentPriceModel).join();
    }

    private CompletableFuture<?> send(FinancialInstrumentPriceModel financialInstrumentPriceModel) {
        EventTopic topic = STOCK_INFO;
        return kafkaTemplate.send(topic.getTopicName(), financialInstrumentPriceModel.symbol(), mapper.mapToDto(financialInstrumentPriceModel))
                .whenComplete((ignored, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish Kafka event to topic {} for symbol {}",
                                topic, financialInstrumentPriceModel.symbol(), ex);
                    }
                });
    }

}
