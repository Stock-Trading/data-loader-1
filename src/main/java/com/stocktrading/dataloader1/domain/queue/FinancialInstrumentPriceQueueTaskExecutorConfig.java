package com.stocktrading.dataloader1.domain.queue;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class FinancialInstrumentPriceQueueTaskExecutorConfig {

    @Bean("financialInstrumentPriceQueueTaskExecutor")
    public TaskExecutor financialInstrumentPriceQueueTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //only one thread to keep FIFO order
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setThreadNamePrefix("price-event-queue-executor-");
        executor.initialize();
        return executor;
    }

}
