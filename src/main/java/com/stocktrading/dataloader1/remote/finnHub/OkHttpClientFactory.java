package com.stocktrading.dataloader1.remote.finnHub;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OkHttpClientFactory {

    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .build();
    }

}
