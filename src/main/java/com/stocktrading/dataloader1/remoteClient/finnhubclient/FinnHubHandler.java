package com.stocktrading.dataloader1.remoteClient.finnhubclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stocktrading.dataloader1.domain.FinnHubApiClientService;
import com.stocktrading.dataloader1.domain.StockPriceModel;
import com.stocktrading.dataloader1.domain.StockPriceService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;


import java.util.List;

@Component
@Log4j2
@AllArgsConstructor
public class FinnHubHandler implements WebSocketHandler, FinnHubApiClientService {

    private final StockPriceMapper mapper;
    private final StockPriceService service;

    private final static ObjectMapper jsonMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String message = "{\"type\":\"subscribe\",\"symbol\":\"AAPL\"}";
        WebSocketMessage<String> webSocketMessage = new TextMessage(message);
        log.info("Trying to subscribe to AAPL price info");
        session.sendMessage(webSocketMessage);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        log.info("Received message: " + message.getPayload() + ". Session id: " + session.getId());
        try {
            if (message.getPayload().toString().equals("{\"type\":\"ping\"}")) {
                String pong = "{\"type\":\"pong\"}";
                WebSocketMessage<String> webSocketMessage = new TextMessage(pong);
                session.sendMessage(webSocketMessage);
                log.info("Sent \"pong\" message");
            }
            FinnHubTradeResponseDto finnHubTradeResponseDto = jsonMapper.readValue(message.getPayload().toString(), FinnHubTradeResponseDto.class);
            log.info("Successfully parsed json into finnHubTradeResponseDto object");
            List<StockPriceModel> stockPriceModelList = finnHubTradeResponseDto.dataList()
                    .stream()
                    .map(mapper::mapToModel)
                    .toList();
            service.publishStockPriceEvent(stockPriceModelList);
        } catch (Exception e) {
            log.error("Caught exception: " + e.getMessage());
        }

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("Error while transporting: " + exception.getMessage() + ". Session id: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("Connection closed. Session id: " + session.getId() + ". Close status: " + closeStatus);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    @Override
    public List<StockPriceModel> getStockPriceDataList(String jsonMessage) {
        return null;
    }
}
