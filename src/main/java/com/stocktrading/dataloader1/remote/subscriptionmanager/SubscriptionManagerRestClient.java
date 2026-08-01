package com.stocktrading.dataloader1.remote.subscriptionmanager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class SubscriptionManagerRestClient {

    @Bean
    public RestClient subscriptionManagerSimpleRestClient() {
        return RestClient.create();
    }

}
