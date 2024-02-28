package com.stocktrading.dataloader1.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import static com.stocktrading.dataloader1.eventPublisher.EventTopic.STOCK_INFO;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic stockTopicFactory() {
        return TopicBuilder.name(STOCK_INFO.name())
                .build();
    }
}
