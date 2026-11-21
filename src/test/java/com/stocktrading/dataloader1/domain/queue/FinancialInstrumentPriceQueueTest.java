package com.stocktrading.dataloader1.domain.queue;

import com.stocktrading.dataloader1.domain.model.FinancialInstrumentPriceModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

@ExtendWith(MockitoExtension.class)
class FinancialInstrumentPriceQueueTest {

    private static FinancialInstrumentPriceModel price(int value) {
        return new FinancialInstrumentPriceModel("SYM", BigDecimal.valueOf(value), LocalDateTime.of(2026, 1, 1, 0, 0));
    }

    @Test
    @DisplayName("""
            given queue with free capacity,
            when prices are put,
            then they should be taken in FIFO order
            """)
    void should() throws InterruptedException {
        // given
        FinancialInstrumentPriceQueue queue = new FinancialInstrumentPriceQueue(3);

        // when
        queue.put(price(1));
        queue.put(price(2));
        queue.put(price(3));

        // then
        assertEquals(price(1), queue.take());
        assertEquals(price(2), queue.take());
        assertEquals(price(3), queue.take());
    }

    @Test
    @DisplayName("""
            given full queue,
            when a new price is put,
            then the oldest price should be dropped and the newest kept
            """)
    void should_dropOldestWhenFull() throws InterruptedException {
        // given
        FinancialInstrumentPriceQueue queue = new FinancialInstrumentPriceQueue(2);
        queue.put(price(1));
        queue.put(price(2));

        // when
        queue.put(price(3));

        // then
        assertEquals(price(2), queue.take());
        assertEquals(price(3), queue.take());
    }

    @Test
    @DisplayName("""
            given queue capacity is zero,
            when the queue is constructed,
            then it should throw an IllegalArgumentException
            """)
    void should_rejectZeroCapacity() {
        // given
        int capacity = 0;

        // when / then
        assertThrows(IllegalArgumentException.class, () -> new FinancialInstrumentPriceQueue(capacity));
    }

    @Test
    @DisplayName("""
            given queue capacity is negative,
            when the queue is constructed,
            then it should throw an IllegalArgumentException
            """)
    void should_rejectNegativeCapacity() {
        // given
        int capacity = -1;

        // when / then
        assertThrows(IllegalArgumentException.class, () -> new FinancialInstrumentPriceQueue(capacity));
    }

    @Test
    @DisplayName("""
            given full queue,
            when many prices are put,
            then put should never block and only the latest prices should remain
            """)
    void should_neverBlockWhenFull() throws InterruptedException {
        // given
        FinancialInstrumentPriceQueue queue = new FinancialInstrumentPriceQueue(2);

        // when
        assertTimeoutPreemptively(Duration.ofSeconds(2), () -> {
            for (int i = 1; i <= 100; i++) {
                queue.put(price(i));
            }
        });

        // then
        assertEquals(price(99), queue.take());
        assertEquals(price(100), queue.take());
    }

    @Test
    @DisplayName("""
            given empty queue and a waiting consumer,
            when a price is put,
            then the blocked take should return that price
            """)
    void should_unblockWaitingTake() throws Exception {
        // given
        FinancialInstrumentPriceQueue queue = new FinancialInstrumentPriceQueue(2);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Future<FinancialInstrumentPriceModel> taken = executor.submit(queue::take);
            assertFalse(taken.isDone());

            // when
            queue.put(price(1));

            // then
            assertEquals(price(1), taken.get(2, TimeUnit.SECONDS));
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    @DisplayName("""
            given small queue and many concurrent producers,
            when they put far more prices than the capacity,
            then no producer should fail and the queue should hold exactly its capacity
            """)
    void should_handleConcurrentProducers() throws Exception {
        // given
        int capacity = 10;
        int producers = 8;
        int pricesPerProducer = 1_000;
        FinancialInstrumentPriceQueue queue = new FinancialInstrumentPriceQueue(capacity);
        ExecutorService executor = Executors.newFixedThreadPool(producers);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<?>> futures = new ArrayList<>();

        // when
        try {
            for (int p = 0; p < producers; p++) {
                futures.add(executor.submit(() -> {
                    start.await();
                    for (int i = 0; i < pricesPerProducer; i++) {
                        queue.put(price(i));
                    }
                    return null;
                }));
            }
            start.countDown();
            for (Future<?> future : futures) {
                future.get(5, TimeUnit.SECONDS);
            }

            // then
            assertTimeoutPreemptively(Duration.ofSeconds(2), () -> {
                for (int i = 0; i < capacity; i++) {
                    queue.take();
                }
            });
            Future<FinancialInstrumentPriceModel> extra = executor.submit(queue::take);
            assertThrows(TimeoutException.class, () -> extra.get(200, TimeUnit.MILLISECONDS));
        } finally {
            executor.shutdownNow();
        }
    }

}
