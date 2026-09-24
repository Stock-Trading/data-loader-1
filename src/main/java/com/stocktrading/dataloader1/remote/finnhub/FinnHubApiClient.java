package com.stocktrading.dataloader1.remote.finnhub;

import com.stocktrading.dataloader1.domain.ports.RemoteSecretsManagerClient;
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

    private final RemoteSecretsManagerClient remoteSecretsManagerClient;

    @Bean
    public WebSocket FinnHubWebSocketClient(OkHttpClient client, FinnHubApiHandler handler) {
        Request request = new Request.Builder()
                .url(buildUrl())
                .build();
        WebSocket webSocket = client.newWebSocket(request, handler);
        log.info("Created FinnHub web socket client");
        return webSocket;
    }

    private String buildUrl() {
        StringBuilder url = new StringBuilder();
        url.append("wss://ws.finnhub.io?token=")
                .append(remoteSecretsManagerClient.getFirstFinnHubApiKey());
        return url.toString();
    }

}
