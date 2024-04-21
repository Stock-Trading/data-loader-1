package com.stocktrading.dataloader1.remoteClient.finnhubclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stocktrading.dataloader1.domain.InstrumentSubscriptionStateChangedEvent;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.WebSocket;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Log4j2
class FinnHubApiSubscriptionEventListener {

    private final WebSocket webSocket;

    private final static ObjectMapper jsonMapper = new ObjectMapper();

    @EventListener
    void onSubscriptionChangeEvent(InstrumentSubscriptionStateChangedEvent event) {
        switch (event.getAction()) {
            case SUBSCRIBED -> prepareAndSendMessage(webSocket, event, FinnHubMessageType.SUBSCRIBE);
            case UNSUBSCRIBED -> prepareAndSendMessage(webSocket, event, FinnHubMessageType.UNSUBSCRIBE);
        }
    }

    private void prepareAndSendMessage(WebSocket webSocket, InstrumentSubscriptionStateChangedEvent event, FinnHubMessageType messageType) {
        FinnHubMessageRequestDto messageRequestDto = FinnHubMessageRequestDto.builder()
                .type(messageType.getMessageType())
                .symbol(event.getFinancialInstrument().symbol())
                .build();
        try {
            webSocket.send(jsonMapper.writeValueAsString(messageRequestDto));
            log.info("Sent message to FinnHub API {} as reaction to event {}", messageRequestDto, event);
        } catch (JsonProcessingException jpe) {
            throw new FinnHubApiClientRuntimeException("Exception while serializing to json: " + jpe.getMessage());
        }
    }

}
