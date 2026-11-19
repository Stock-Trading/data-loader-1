package com.stocktrading.dataloader1.domain.queue;

import com.stocktrading.dataloader1.domain.model.FinancialInstrumentPriceModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Queue used to store price events {@link FinancialInstrumentPriceModel} in FIFO order.
 * It allows for asynchronous processing of various steps.
 *
 * @see FinancialInstrumentPriceModel
 */
@Slf4j
@Component
public class FinancialInstrumentPriceQueue {

    private final ArrayDeque<FinancialInstrumentPriceModel> buffer;
    private final int capacity;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();

    FinancialInstrumentPriceQueue(@Value("${data-loader.price-queue.capacity:100000}") int capacity) {
        this.capacity = capacity;
        this.buffer = new ArrayDeque<>(Math.min(capacity, 1024));
    }

    /**
     * Adds a price event to the tail of the queue without ever blocking the caller.
     * <p>
     * If the queue is full, the oldest event (the head) is removed to make room for the new one
     * ("drop oldest" policy), so the freshest prices always win. The size check, eviction and insertion
     * happen under one lock, so a concurrent {@link #take()} can never cause a newer event to be dropped
     * unnecessarily. A warning is logged only when an event was actually dropped.
     *
     * @param priceModel price event to enqueue
     */
    public void put(FinancialInstrumentPriceModel priceModel) {
        lock.lock();
        try {
            if (buffer.size() >= capacity) {
                buffer.pollFirst();
                log.warn("Price queue full, dropped oldest price event");
            }
            buffer.addLast(priceModel);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Removes and returns the oldest price event, waiting (and releasing the lock) while the queue is empty.
     *
     * @throws InterruptedException if interrupted while waiting
     */
    public FinancialInstrumentPriceModel take() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            while (buffer.isEmpty()) {
                notEmpty.await();
            }
            return buffer.pollFirst();
        } finally {
            lock.unlock();
        }
    }

}
