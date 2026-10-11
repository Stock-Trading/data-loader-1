package com.stocktrading.dataloader1.remote.kafkaeventpublisher;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
class KafkaPublisherAsyncConfig {

    static final String KAFKA_PUBLISHER_EXECUTOR = "kafkaPublisherExecutor";

    @Bean(KAFKA_PUBLISHER_EXECUTOR)
    TaskExecutor kafkaPublisherExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(10_000);
        executor.setThreadNamePrefix("kafka-publisher-");
        return executor;
    }
}
