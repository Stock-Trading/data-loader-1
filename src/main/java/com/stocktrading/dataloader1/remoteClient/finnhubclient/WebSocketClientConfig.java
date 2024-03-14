package com.stocktrading.dataloader1.remoteClient.finnhubclient;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

@Configuration
@Log4j2
public class WebSocketClientConfig {

    @Bean
    public WebSocketClient getStandardWebSocketClient(){
        return new StandardWebSocketClient();
    }

}
