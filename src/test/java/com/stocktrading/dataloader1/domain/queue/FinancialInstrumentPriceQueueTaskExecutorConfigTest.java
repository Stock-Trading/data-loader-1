package com.stocktrading.dataloader1.domain.queue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class FinancialInstrumentPriceQueueTaskExecutorConfigTest {

    private ThreadPoolTaskExecutor executor;

    @AfterEach
    void tearDown() {
        if (executor != null) {
            executor.shutdown();
        }
    }

    @Test
    @DisplayName("""
        given the task executor config,
        when financialInstrumentPriceQueueTaskExecutor is called,
        then it should return a ThreadPoolTaskExecutor with exactly one thread
        """)
    void should() {
        // given
        FinancialInstrumentPriceQueueTaskExecutorConfig config = new FinancialInstrumentPriceQueueTaskExecutorConfig();

        // when
        TaskExecutor result = config.financialInstrumentPriceQueueTaskExecutor();
        executor = assertInstanceOf(ThreadPoolTaskExecutor.class, result, "Executor should be a ThreadPoolTaskExecutor");

        // then
        assertEquals(1, executor.getCorePoolSize(), "Core pool size should be 1 to keep FIFO order");
        assertEquals(1, executor.getMaxPoolSize(), "Max pool size should be 1 to keep FIFO order");
        assertEquals("price-event-queue-executor-", executor.getThreadNamePrefix(),
            "Thread name prefix should match the expected value");
    }

    @Test
    @DisplayName("""
        given the configured executor,
        when multiple tasks are submitted,
        then they should run sequentially in submission order on a thread with the configured name prefix
        """)
    void shouldExecuteTasksInFifoOrder() throws InterruptedException {
        // given
        executor = (ThreadPoolTaskExecutor) new FinancialInstrumentPriceQueueTaskExecutorConfig()
            .financialInstrumentPriceQueueTaskExecutor();
        List<Integer> order = new ArrayList<>();
        List<String> threadNames = new ArrayList<>();
        int tasks = 20;
        CountDownLatch latch = new CountDownLatch(tasks);

        // when
        for (int i = 0; i < tasks; i++) {
            int index = i;
            executor.execute(() -> {
                order.add(index);
                threadNames.add(Thread.currentThread().getName());
                latch.countDown();
            });
        }

        // then
        assertTrue(latch.await(5, TimeUnit.SECONDS), "All tasks should complete in time");
        assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19), order,
            "Tasks should be executed in FIFO order");
        assertEquals(1, threadNames.stream().distinct().count(), "All tasks should run on a single thread");
        assertTrue(threadNames.getFirst().startsWith("price-event-queue-executor-"),
            "Thread name should start with the configured prefix");
    }

    @Test
    @DisplayName("""
        given the task executor config,
        when its bean method annotation is inspected,
        then the bean should be named financialInstrumentPriceQueueTaskExecutor
        """)
    void shouldDeclareExpectedBeanName() throws NoSuchMethodException {
        // given
        var method = FinancialInstrumentPriceQueueTaskExecutorConfig.class
            .getMethod("financialInstrumentPriceQueueTaskExecutor");

        // when
        Bean bean = method.getAnnotation(Bean.class);

        // then
        assertArrayEquals(new String[]{"financialInstrumentPriceQueueTaskExecutor"}, bean.value(),
            "Bean name should stay unchanged because it is used for qualification");
    }

}
