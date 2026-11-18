package com.stocktrading.dataloader1.remote.finnhub;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
class FinnHubMapperTaskExecutorConfig {

    static final String FINN_HUB_MAPPER_EXECUTOR = "finnHubMapperExecutor";

    @Bean(FINN_HUB_MAPPER_EXECUTOR)
    TaskExecutor finnHubMapperExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //one thread to ensure FIFO order
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(100_000);
        executor.setThreadNamePrefix("finnHub-mapper-");
        return executor;
    }

}
