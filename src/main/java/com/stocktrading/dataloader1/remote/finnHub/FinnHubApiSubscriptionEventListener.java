package com.stocktrading.dataloader1.remote.finnHub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stocktrading.dataloader1.domain.event.FinancialInstrumentSubscriptionStateChangedEvent;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.WebSocket;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@AllArgsConstructor
@Log4j2
public class FinnHubApiSubscriptionEventListener {

    private final ClientsManager clientsManager;

    private final static ObjectMapper jsonMapper = new ObjectMapper();

    @EventListener
    void onSubscriptionStateChangeEvent(FinancialInstrumentSubscriptionStateChangedEvent event) {
        ConcurrentHashMap<WebSocket, List<String>> mapOfWebSocketClientsAndItsSubscribedFinancialInstruments = clientsManager.getWebSocketAndFinancialInstrumentsListConcurrentHashMap();
        switch (event.getAction()) {
            case SUBSCRIBED -> subscribe(mapOfWebSocketClientsAndItsSubscribedFinancialInstruments, event);
            case UNSUBSCRIBED -> unsubscribe(mapOfWebSocketClientsAndItsSubscribedFinancialInstruments, event);
        }
    }

    private void unsubscribe(ConcurrentHashMap<WebSocket, List<String>> map, FinancialInstrumentSubscriptionStateChangedEvent event) {
        for (Map.Entry<WebSocket, List<String>> entry : map.entrySet()) {
            if (entry.getValue().contains(event.getFinancialInstrument().symbol())) {
                prepareAndSendMessage(entry.getKey(), event, FinnHubMessageType.UNSUBSCRIBE);
            }
        }
    }

    private void subscribe(ConcurrentHashMap<WebSocket, List<String>> map, FinancialInstrumentSubscriptionStateChangedEvent event) {
        for (Map.Entry<WebSocket, List<String>> entry : map.entrySet()) {
            if (entry.getValue().size() < 5) {
                prepareAndSendMessage(entry.getKey(), event, FinnHubMessageType.SUBSCRIBE);
            } else {
                log.info("Web socket client {} already listens to max number of financial instruments", entry.getKey().toString());
            }
        }
    }

    private void prepareAndSendMessage(WebSocket webSocket, FinancialInstrumentSubscriptionStateChangedEvent event, FinnHubMessageType messageType) {
        FinnHubMessageRequestDto messageRequestDto = FinnHubMessageRequestDto.builder()
                .type(messageType.getMessageType())
                .symbol(event.getFinancialInstrument().symbol())
                .build();

        try {
            webSocket.send(jsonMapper.writeValueAsString(messageRequestDto));
            log.info("Sent message {} to FinnHub API as reaction to event {}", messageRequestDto, event);
        } catch (JsonProcessingException jpe) {
            throw new FinnHubApiClientRuntimeException("Exception while serializing to json: " + jpe.getMessage());
        }
    }
}
