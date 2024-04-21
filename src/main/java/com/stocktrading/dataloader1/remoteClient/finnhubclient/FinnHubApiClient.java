package com.stocktrading.dataloader1.remoteClient.finnhubclient;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Log4j2
public class FinnHubApiClient {

    private final OkHttpClient client;
    private final FinnHubApiHandler handler;

    private final static String uri = "wss://ws.finnhub.io?token=cnli7c9r01qk2u6r38j0cnli7c9r01qk2u6r38jg";

    @Bean
    public WebSocket connectToFinnHubApi() {
        Request request = new Request.Builder()
                .url(uri)
                .build();
        WebSocket webSocket = client.newWebSocket(request, handler);
        log.info("Connected to FinnHub API");
        return webSocket;
    }
//TODO jak dynamicznie uruchamiać i wyłączać kolejne websockety: klasa zarządzająca klientami WebSocketowymi

    @PostConstruct
    public void init() {
        connectToFinnHubApi();
    }
}
