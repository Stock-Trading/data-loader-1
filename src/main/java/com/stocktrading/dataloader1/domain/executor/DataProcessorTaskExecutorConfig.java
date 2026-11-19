package com.stocktrading.dataloader1.domain.executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class DataProcessorTaskExecutorConfig {

    static final String DATA_PROCESSOR_TASK_EXECUTOR = "dataProcessorTaskExecutor";

    @Bean(DATA_PROCESSOR_TASK_EXECUTOR)
    TaskExecutor finnHubMapperExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //one thread to ensure FIFO order
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(100_000);
        executor.setThreadNamePrefix("data-processor-");
        return executor;
    }

}
