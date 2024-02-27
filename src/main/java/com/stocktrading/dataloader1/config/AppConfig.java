package com.stocktrading.dataloader1.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.web.client.RestClient;

@Configuration
public class AppConfig {

    @Bean
    public RestClient restClientFactory() {
        return RestClient.builder()
                .build();
    }

    @Bean
    public NewTopic stockTopicFactory() {
        return TopicBuilder.name("stockInfo")
                .build();
    }
}
