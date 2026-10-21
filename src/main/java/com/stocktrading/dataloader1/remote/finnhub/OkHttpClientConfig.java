package com.stocktrading.dataloader1.remote.finnhub;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class OkHttpClientConfig {

    @Bean
    OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder().build();
    }

}
