package com.stocktrading.dataloader1.remote.finnhub;

import com.stocktrading.dataloader1.domain.event.FinancialInstrumentSubscriptionStateChangedEvent;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.WebSocket;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
@AllArgsConstructor
@Log4j2
class FinnHubApiSubscriptionEventListener {

    private final WebSocket finnHubWebSocketClient;

    private final ObjectMapper jsonMapper;

    @EventListener
    void onSubscriptionStateChangeEvent(FinancialInstrumentSubscriptionStateChangedEvent event) {
        switch (event.getAction()) {
            case SUBSCRIBED -> prepareAndSendMessage(finnHubWebSocketClient, event, FinnHubMessageType.SUBSCRIBE);
            case UNSUBSCRIBED -> prepareAndSendMessage(finnHubWebSocketClient, event, FinnHubMessageType.UNSUBSCRIBE);
        }
    }

    private void prepareAndSendMessage(WebSocket webSocket,
                                       FinancialInstrumentSubscriptionStateChangedEvent event,
                                       FinnHubMessageType messageType) {
        FinnHubMessageRequest messageRequestDto = FinnHubMessageRequest.builder()
                .type(messageType.getMessageType())
                .symbol(event.getFinancialInstrument().symbol())
                .build();
        try {
            String message = jsonMapper.writeValueAsString(messageRequestDto);
            if (!webSocket.send(message)) {
                throw new FinnHubApiClientRuntimeException("FinnHub WebSocket rejected message: " + messageRequestDto);
            }
            log.info("Sent message to FinnHub API {} as reaction to event {}", messageRequestDto, event);
        } catch (JacksonException exception) {
            throw new FinnHubApiClientRuntimeException("Exception while serializing to json: " + exception.getMessage());
        }
    }

}
