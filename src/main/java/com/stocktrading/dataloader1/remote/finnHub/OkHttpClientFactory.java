package com.stocktrading.dataloader1.remote.finnHub;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class OkHttpClientFactory {

    public OkHttpClient okHttpClient() {

        return new OkHttpClient.Builder()
                .build();
    }

}
