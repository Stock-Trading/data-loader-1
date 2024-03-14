package com.stocktrading.dataloader1.remoteClient.finnhubclient;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.WebSocketClient;

@Component
@AllArgsConstructor
@Log4j2
public class FinnHubApiClient {

    private final WebSocketClient webSocketClient;
    private final FinnHubHandler finnHubHandler;
    private final static String uri = "wss://ws.finnhub.io?token=cnli7c9r01qk2u6r38j0cnli7c9r01qk2u6r38jg";

    public void connectToFinnHubApi() {
        log.info("Trying to establish connection with FinnHub via WebSocket");
        webSocketClient.execute(finnHubHandler, uri);
    }

    @PostConstruct
    public void init() {
        connectToFinnHubApi();
    }
}
