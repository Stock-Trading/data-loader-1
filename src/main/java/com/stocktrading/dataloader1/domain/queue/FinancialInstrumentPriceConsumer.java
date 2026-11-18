package com.stocktrading.dataloader1.domain.queue;

import com.stocktrading.dataloader1.domain.model.FinancialInstrumentPriceModel;
import com.stocktrading.dataloader1.domain.ports.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FinancialInstrumentPriceConsumer {

    private final FinancialInstrumentPriceQueue queue;
    private final TaskExecutor financialInstrumentPriceQueueTaskExecutor;
    private final EventPublisher eventPublisher;

    public FinancialInstrumentPriceConsumer(FinancialInstrumentPriceQueue queue,
                                            @Qualifier("financialInstrumentPriceQueueTaskExecutor") TaskExecutor financialInstrumentPriceQueueTaskExecutor,
                                            EventPublisher eventPublisher) {
        this.queue = queue;
        this.financialInstrumentPriceQueueTaskExecutor = financialInstrumentPriceQueueTaskExecutor;
        this.eventPublisher = eventPublisher;
    }

    public FinancialInstrumentPriceModel consume() throws InterruptedException {
        FinancialInstrumentPriceModel priceModel = queue.take();
        log.info("Consumed {} from the queue", priceModel);
        return priceModel;
    }

    @EventListener(ApplicationStartedEvent.class)
    void consumeAllTheTime() {
        financialInstrumentPriceQueueTaskExecutor.execute(() -> {
            while (true) {
                try {
                    FinancialInstrumentPriceModel price = consume();
                    eventPublisher.publishFinancialInstrumentPriceInfoEvent(price);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

}
