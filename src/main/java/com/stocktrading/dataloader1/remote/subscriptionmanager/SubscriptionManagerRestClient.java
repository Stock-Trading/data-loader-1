package com.stocktrading.dataloader1.remote.subscriptionmanager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
class SubscriptionManagerRestClient {

    @Bean
    RestClient subscriptionManagerSimpleRestClient() {
        return RestClient.create();
    }

}
