package com.stocktrading.dataloader1.domain.queue;

import com.stocktrading.dataloader1.domain.model.FinancialInstrumentPriceModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Queue used to store price events {@link FinancialInstrumentPriceModel} in FIFO order.
 * It allows for asynchronous processing of various steps.
 *
 * @see FinancialInstrumentPriceModel
 */
@Slf4j
@Component
public class FinancialInstrumentPriceQueue {

    private final BlockingQueue<FinancialInstrumentPriceModel> queue;

    public FinancialInstrumentPriceQueue() {
        this(100_000);
    }

    FinancialInstrumentPriceQueue(int capacity) {
        this.queue = new LinkedBlockingQueue<>(capacity);
    }

    /**
     * Adds a price event to the tail of the queue without ever blocking the caller.
     * <p>
     * If the queue is full, the oldest event (the head) is removed to make room for the new one
     * ("drop oldest" policy), so the freshest prices always win. The add is retried in a loop because
     * another thread may take the freed slot between {@code poll()} and {@code offer()}.
     * A warning is logged only when an event was actually dropped.
     *
     * @param priceModel price event to enqueue
     */
    public void put(FinancialInstrumentPriceModel priceModel) {
        while (!queue.offer(priceModel)) {
            if (queue.poll() != null) {
                log.warn("Price queue full, dropped oldest price event");
            }
        }
    }

    public FinancialInstrumentPriceModel take() throws InterruptedException {
        return queue.take();
    }

}
