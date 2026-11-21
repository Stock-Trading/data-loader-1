package com.stocktrading.dataloader1.domain.queue;

import com.stocktrading.dataloader1.domain.model.FinancialInstrumentPriceModel;
import com.stocktrading.dataloader1.domain.ports.EventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.TaskExecutor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FinancialInstrumentPriceConsumerTest {

    private static final FinancialInstrumentPriceModel FIRST = price(1);
    private static final FinancialInstrumentPriceModel SECOND = price(2);

    @Mock
    private FinancialInstrumentPriceQueue queue;

    @Mock
    private EventPublisher eventPublisher;

    private final CountDownLatch loopFinished = new CountDownLatch(1);
    private final AtomicBoolean interruptFlagAfterLoop = new AtomicBoolean();

    private FinancialInstrumentPriceConsumer consumer;

    private static FinancialInstrumentPriceModel price(int value) {
        return new FinancialInstrumentPriceModel("SYM", BigDecimal.valueOf(value), LocalDateTime.of(2026, 1, 1, 0, 0));
    }

    @BeforeEach
    void setUp() {
        // runs the task on a separate thread, like the real executor, and records the thread's state once the task ends
        TaskExecutor executor = task -> new Thread(() -> {
            task.run();
            interruptFlagAfterLoop.set(Thread.currentThread().isInterrupted());
            loopFinished.countDown();
        }).start();
        consumer = new FinancialInstrumentPriceConsumer(queue, executor, eventPublisher);
    }

    @Test
    @DisplayName("""
        given a price available in the queue,
        when consume is called,
        then it should return the price taken from the queue
        """)
    void should() throws InterruptedException {
        // given
        given(queue.take()).willReturn(FIRST);

        // when
        FinancialInstrumentPriceModel result = consumer.consume();

        // then
        assertEquals(FIRST, result, "Consumed price should be the one taken from the queue");
    }

    @Test
    @DisplayName("""
        given the queue throws InterruptedException,
        when consume is called,
        then the exception should be propagated to the caller
        """)
    void shouldPropagateInterruptedException() throws InterruptedException {
        // given
        given(queue.take()).willThrow(new InterruptedException());

        // when
        // then
        assertThrows(InterruptedException.class, () -> consumer.consume(),
            "InterruptedException should be propagated");
    }

    @Test
    @DisplayName("""
        given prices in the queue followed by an interruption,
        when consumeAllTheTime is called,
        then it should publish every price in FIFO order and then stop with the interrupt flag restored
        """)
    void shouldPublishPricesInOrderAndStopOnInterrupt() throws InterruptedException {
        // given
        given(queue.take()).willReturn(FIRST).willReturn(SECOND).willThrow(new InterruptedException());

        // when
        consumer.consumeAllTheTime();

        // then
        assertTrue(loopFinished.await(5, TimeUnit.SECONDS), "Consuming loop should end after interruption");
        InOrder inOrder = inOrder(eventPublisher);
        inOrder.verify(eventPublisher).publishFinancialInstrumentPriceInfoEvent(FIRST);
        inOrder.verify(eventPublisher).publishFinancialInstrumentPriceInfoEvent(SECOND);
        assertTrue(interruptFlagAfterLoop.get(), "Interrupt flag should be restored after InterruptedException");
    }

    @Test
    @DisplayName("""
        given the publisher fails for the first price,
        when consumeAllTheTime is called,
        then it should keep looping and publish the next price
        """)
    void shouldContinueAfterPublisherFailure() throws InterruptedException {
        // given
        given(queue.take()).willReturn(FIRST).willReturn(SECOND).willThrow(new InterruptedException());
        org.mockito.BDDMockito.willThrow(new RuntimeException("kafka down"))
            .given(eventPublisher).publishFinancialInstrumentPriceInfoEvent(FIRST);

        // when
        consumer.consumeAllTheTime();

        // then
        verify(eventPublisher, timeout(5000)).publishFinancialInstrumentPriceInfoEvent(SECOND);
        assertTrue(loopFinished.await(5, TimeUnit.SECONDS), "Consuming loop should end after interruption");
    }

    @Test
    @DisplayName("""
        given the queue fails with a non-interrupt exception once,
        when consumeAllTheTime is called,
        then it should keep looping and publish the next price
        """)
    void shouldContinueAfterQueueFailure() throws InterruptedException {
        // given
        given(queue.take()).willThrow(new IllegalStateException("boom")).willReturn(FIRST)
            .willThrow(new InterruptedException());

        // when
        consumer.consumeAllTheTime();

        // then
        verify(eventPublisher, timeout(5000)).publishFinancialInstrumentPriceInfoEvent(FIRST);
        assertTrue(loopFinished.await(5, TimeUnit.SECONDS), "Consuming loop should end after interruption");
    }

}
